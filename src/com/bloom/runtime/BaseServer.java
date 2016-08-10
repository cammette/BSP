package com.bloom.runtime;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Endpoint;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.Member;
import com.bloom.distribution.WAQueue;
import com.bloom.distribution.WAQueue.Listener;
import com.bloom.exception.ServerException;
import com.bloom.exceptionhandling.WAExceptionMgr;
import com.bloom.messaging.MessagingProvider;
import com.bloom.messaging.MessagingSystem;
import com.bloom.metaRepository.HazelcastSingleton;
import com.bloom.metaRepository.MDRepository;
import com.bloom.metaRepository.MetaDataDbFactory;
import com.bloom.metaRepository.MetaDataDbProvider;
import com.bloom.metaRepository.MetaDataRepositoryException;
import com.bloom.metaRepository.MetadataRepository;
import com.bloom.metaRepository.RemoteCall;
import com.bloom.proc.events.ShowStreamEvent;
import com.bloom.runtime.channels.BroadcastAsyncChannel;
import com.bloom.runtime.channels.Channel;
import com.bloom.runtime.channels.SimpleChannel;
import com.bloom.runtime.channels.ZMQChannel;
import com.bloom.runtime.components.EntityType;
import com.bloom.runtime.components.Flow;
import com.bloom.runtime.components.FlowComponent;
import com.bloom.runtime.components.Link;
import com.bloom.runtime.components.Publisher;
import com.bloom.runtime.components.Stream;
import com.bloom.runtime.components.Subscriber;
import com.bloom.runtime.meta.MetaInfo;
import com.bloom.runtime.meta.MetaInfo.DeploymentGroup;
import com.bloom.runtime.meta.MetaInfo.Initializer;
import com.bloom.runtime.meta.MetaInfo.MetaObject;
import com.bloom.runtime.meta.MetaInfo.Type;
import com.bloom.runtime.monitor.MonitorApp;
import com.bloom.runtime.monitor.MonitorEvent;
import com.bloom.runtime.monitor.MonitorModel;
import com.bloom.security.WASecurityManager;
import com.bloom.uuid.UUID;
import com.bloom.wactionstore.WActionStores;
import com.bloom.wactionstore.elasticsearch.WActionStoreManager;
import java.io.File;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javassist.Modifier;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequestBuilder;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.transport.TransportStats;
import org.jctools.queues.MpscCompoundQueue;

public abstract class BaseServer
  implements WAQueue.Listener, ServerServices
{
  // 全局命名空间
  public static final String GLOBAL_NAMSPACE = "Global";
  // 全局命名空间的 UUID
  public static final UUID GLOBAL_NAMSPACE_UUID = new UUID("9A315652-1204-6E32-9105-AEC16CC6AD49");
  // 监控源应用
  public static final String MONITORING_SOURCE_APP = "MonitoringSourceApp";
  // 监控源数据流
  public static final String MONITORING_SOURCE_FLOW = "MonitoringSourceFlow";
  // 监控源
  public static final String MONITORING_SOURCE = "MonitoringSource1";
  // 监控处理应用
  public static final String MONITORING_PROCESS_APP = "MonitoringProcessApp";
  // 服务器名称
  private static String serverName = "__server";
  // 设置管理
  public static final String ADMIN = "admin";
  private static Logger logger = Logger.getLogger(BaseServer.class);
  // 服务器启动时间
  public static final long startupTimeStamp = System.currentTimeMillis();
  // 元数据 DB 提供者
  public static MetaDataDbProvider metaDataDbProvider;
  // 调度器
  private final ScheduledThreadPoolExecutor scheduler;
  public final Map<UUID, FlowComponent> openObjects;
  // 消息系统
  public MessagingSystem messagingSystem;
  // 异常管理器
  public WAExceptionMgr exception_manager;
  // 元数据容器
  public MDRepository metadataRepository;
  // 显示数据处理流
  private Stream showStream;
  public static volatile BaseServer baseServer;
  // 异常数据流
  private Stream exceptionStream;
  
  public static class LogAndDiscardPolicy
    implements RejectedExecutionHandler
  {
    public void rejectedExecution(Runnable task, ThreadPoolExecutor executor)
    {
      BaseServer.logger.warn("Task rejected from BaseServer.scheduler: Task: " + task.getClass().getName() + " ; Executor status: " + executor.toString());
    }
  }
  
  public BaseServer()
  {
    this.openObjects = new ConcurrentHashMap();
    this.scheduler = new ScheduledThreadPoolExecutor(4, new CustomThreadFactory("BaseServer_Scheduler"), new LogAndDiscardPolicy());
    
    this.scheduler.setRemoveOnCancelPolicy(true);
  }
  
  public static BaseServer getBaseServer()
  {
    return baseServer;
  }
  // 设置元数据容器的相关信息, 获得保存数据库元数据的容器
  public static MetaDataDbProvider setMetaDataDbProviderDetails()
  {
    String dataBaseName = System.getProperty("com.bloom.config.metaDataRepositoryDB");
    if ((dataBaseName == null) || (dataBaseName.isEmpty())) {
      dataBaseName = "derby";
    }
    metaDataDbProvider = MetaDataDbFactory.getOurMetaDataDb(dataBaseName);
    return metaDataDbProvider;
  }
  
  public static MetaDataDbProvider getMetaDataDBProviderDetails()
  {
    return metaDataDbProvider;
  }
  // 初始化元数据
  public void initializeMetaData()
  {
    this.metadataRepository = MetadataRepository.getINSTANCE();
    // 首先选择一个候选服务器
    String candidateServerName = null;
    try
    {
      candidateServerName = generateServerName();
    }
    catch (Exception e)
    {
      logger.error(e.getMessage());
      System.exit(0);
    }
    serverName = candidateServerName;
  }
  // 初始化消息系统, 默认为ZeroMQ
  public void initializeMessaging()
  {
    this.messagingSystem = MessagingProvider.getMessagingSystem("com.bloom.jmqmessaging.ZMQSystem");
  }
  // 获得消息系统
  public MessagingSystem getMessagingSystem()
  {
    return this.messagingSystem;
  }
  // 获得异常数据流
  public Stream getExceptionStream()
    throws Exception
  {
    return this.exceptionStream;
  }
  
  public Stream getShowStream()
  {
    return this.showStream;
  }
  
  protected MetaInfo.Type getTypeForCLass(Class<?> clazz)
    throws MetaDataRepositoryException
  {
    MetaInfo.Type type = null;
    String typeName = "Global." + clazz.getSimpleName();
    // 通过类型名称得到元信息的对象
    try
    {
      type = (MetaInfo.Type)this.metadataRepository.getMetaObjectByName(EntityType.TYPE, "Global", clazz.getSimpleName(), null, WASecurityManager.TOKEN);
    }
    catch (Exception e)
    {
      if (logger.isInfoEnabled()) {
        logger.info(e.getLocalizedMessage());
      }
      return null;
    }
    if (type == null)
    {
      Map<String, String> fields = new LinkedHashMap();
      Field[] cFields = clazz.getDeclaredFields();
      for (Field f : cFields) {
        if (Modifier.isPublic(f.getModifiers())) {
          fields.put(f.getName(), f.getType().getCanonicalName());
        }
      }
      type = new MetaInfo.Type();
      type.construct(typeName, MetaInfo.GlobalNamespace, clazz.getName(), fields, null, false);
      putObject(type);
    }
    return type;
  }
  
  public void initExceptionHandler()
  {
    try
    {
      if (logger.isInfoEnabled()) {
        logger.info("creating exception manager");
      }
      this.exception_manager = WAExceptionMgr.get();
      // 构建异常流
      MetaInfo.Type dataType = getTypeForCLass(ExceptionEvent.class);
      final MetaInfo.Stream streamMetaObj = new MetaInfo.Stream();
      streamMetaObj.construct("exceptionsStream", WAExceptionMgr.ExceptionStreamUUID, MetaInfo.GlobalNamespace, dataType.uuid, null, null, null);
      // 将数据流的元对象放入到元数据的容器中
      this.metadataRepository.putMetaObject(streamMetaObj, WASecurityManager.TOKEN);
      this.exceptionStream = ((Stream)putOpenObjectIfNotExists(streamMetaObj.uuid, new StreamObjectFac()
      {
        public FlowComponent create()
          throws Exception
        {
          Stream s = new Stream(streamMetaObj, BaseServer.this, null);
          return s;
        }
      }));
      // 启动异常处理流
      this.exceptionStream.start();
      subscribe(this.exceptionStream, this.exception_manager);
      if (logger.isDebugEnabled()) {
        logger.debug("exception stream, control stream are created");
      }
    }
    catch (Exception se)
    {
      logger.error("error" + se);
      se.printStackTrace();
    }
  }
  
  public void closeExceptionStream()
  {
    try
    {
      unsubscribe(this.exceptionStream, this.exception_manager);
      this.exceptionStream.close();
    }
    catch (Exception e)
    {
      logger.warn("Error in closing ExceptionStream : " + e.getMessage());
    }
  }
  // 初始化显示流
  public void initShowStream()
  {
    try
    {
      if (logger.isInfoEnabled()) {
        logger.info("Creating SHOW Stream");
      }
      MetaInfo.Type dataType = getTypeForCLass(ShowStreamEvent.class);
      final MetaInfo.Stream streamMetaObj = new MetaInfo.Stream();
      streamMetaObj.construct("showStream", ShowStreamManager.ShowStreamUUID, MetaInfo.GlobalNamespace, dataType.uuid, null, null, null);
      // 放入流元对象
      this.showStream = ((Stream)putOpenObjectIfNotExists(streamMetaObj.uuid, new StreamObjectFac()
      {
        public FlowComponent create()
          throws Exception
        {
          Stream s = new Stream(streamMetaObj, BaseServer.this, null);
          return s;
        }
      }));
      this.showStream.start();
      if (logger.isDebugEnabled()) {
        logger.debug("exception stream, control stream are created");
      }
    }
    catch (Exception se)
    {
      logger.error("error" + se);
      se.printStackTrace();
    }
  }
  // 初始化监控 App
  public void initMonitoringApp()
    throws MetaDataRepositoryException
  {
    this.monitorLogger = Logger.getRootLogger();
    this.monitorAppender = new MonitorLogAppender(this);
    this.monitorAppender.setThreshold(Level.WARN);
    this.monitorLogger.addAppender(this.monitorAppender);
    MonitorApp.getMonitorApp();
  }
  // 通过 UUID 获得元对象
  public MetaInfo.MetaObject getMetaObject(UUID uuid)
    throws MetaDataRepositoryException
  {
    return this.metadataRepository.getMetaObjectByUUID(uuid, WASecurityManager.TOKEN);
  }
  // 获得类型信息
  public MetaInfo.Type getTypeInfo(UUID uuid)
    throws ServerException, MetaDataRepositoryException
  {
    return (MetaInfo.Type)getObjectInfo(uuid, EntityType.TYPE);
  }
  // 通过名称获得部署组
  public MetaInfo.DeploymentGroup getDeploymentGroupByName(String name)
    throws MetaDataRepositoryException
  {
    return (MetaInfo.DeploymentGroup)getObject(EntityType.DG, "Global", name);
  }
  // 放入元数据对象
  protected MetaInfo.MetaObject putObject(MetaInfo.MetaObject obj)
    throws MetaDataRepositoryException
  {
    this.metadataRepository.putMetaObject(obj, WASecurityManager.TOKEN);
    return this.metadataRepository.getMetaObjectByUUID(obj.getUuid(), WASecurityManager.TOKEN);
  }
  // 更新元数据对象
  protected MetaInfo.MetaObject updateObject(MetaInfo.MetaObject obj)
    throws MetaDataRepositoryException
  {
    this.metadataRepository.updateMetaObject(obj, WASecurityManager.TOKEN);
    return this.metadataRepository.getMetaObjectByUUID(obj.getUuid(), WASecurityManager.TOKEN);
  }
  
  protected MetaInfo.MetaObject getObject(EntityType type, String namespace, String name)
    throws MetaDataRepositoryException
  {
    return this.metadataRepository.getMetaObjectByName(type, namespace, name, null, WASecurityManager.TOKEN);
  }
  
  public MetaInfo.MetaObject getObject(UUID uuid)
    throws MetaDataRepositoryException
  {
    return this.metadataRepository.getMetaObjectByUUID(uuid, WASecurityManager.TOKEN);
  }
  // 通过 ID 获得部署组
  public MetaInfo.DeploymentGroup getDeploymentGroupByID(UUID uuid)
    throws ServerException, MetaDataRepositoryException
  {
    return (MetaInfo.DeploymentGroup)getObjectInfo(uuid, EntityType.DG);
  }
  // 订阅器订阅消息
  public void subscribe(Publisher pub, Subscriber sub)
    throws Exception
  {
    subscribe(pub, new Link(sub));
  }
  
  public void subscribe(Publisher pub, Link link)
    throws Exception
  {
    Channel c = pub.getChannel();
    subscribe(c, link);
  }
  
  public void subscribe(Channel c, Link link)
    throws Exception
  {
    c.addSubscriber(link);
  }
  
  public void unsubscribe(Publisher pub, Subscriber sub)
    throws Exception
  {
    unsubscribe(pub, new Link(sub));
  }
  
  public void unsubscribe(Publisher pub, Link link)
    throws Exception
  {
    Channel c = pub.getChannel();
    if (c != null) {
      unsubscribe(c, link);
    }
  }
  
  public void unsubscribe(Channel c, Link link)
    throws Exception
  {
    c.removeSubscriber(link);
  }
  
  public Channel createSimpleChannel()
  {
    return new SimpleChannel();
  }
  // 获得流信息
  public MetaInfo.Stream getStreamInfo(UUID uuid)
    throws ServerException, MetaDataRepositoryException
  {
    return (MetaInfo.Stream)getObjectInfo(uuid, EntityType.STREAM);
  }
  
  public ScheduledThreadPoolExecutor getScheduler()
  {
    return this.scheduler;
  }
  //关闭调度器和消息提供器
  public void shutdown()
  {
    this.scheduler.shutdown();
    MessagingProvider.shutdownAll();
    try
    {
      if (!this.scheduler.awaitTermination(5L, TimeUnit.SECONDS))
      {
    	  	// 关闭调度器
        this.scheduler.shutdownNow();
        if (!this.scheduler.awaitTermination(60L, TimeUnit.SECONDS)) {
          System.err.println("Scheduler did not terminate");
        }
      }
    }
    catch (InterruptedException ie)
    {
      this.scheduler.shutdown();
      Thread.currentThread().interrupt();
    }
  }
  
  public void putOpenObject(FlowComponent obj)
    throws MetaDataRepositoryException
  {
    UUID id = obj.getMetaID();
    synchronized (this.openObjects)
    {
      this.openObjects.put(id, obj);
      // 放入开放对象
      Pair pair = Pair.make(id, getServerID());
      this.metadataRepository.putDeploymentInfo(pair, WASecurityManager.TOKEN);
    }
  }
  // 将流组件放入开放对象
  protected FlowComponent putOpenObjectIfNotExists(UUID objId, StreamObjectFac fac)
    throws Exception
  {
    synchronized (this.openObjects)
    {
      FlowComponent obj = (FlowComponent)this.openObjects.get(objId);
      if (obj == null)
      {
        obj = fac.create();
        if (!this.openObjects.containsKey(objId)) {
          putOpenObject(obj);
        } else {
          assert (this.openObjects.containsKey(objId));
        }
      }
      return obj;
    }
  }
  
  // 根据 UUID 获得开放对象
  public FlowComponent getOpenObject(UUID uuid)
  {
	  
	  FlowComponent flowComponent;
	try {
		flowComponent = this.openObjects.get(uuid);
	} catch (Exception e) {
		e.printStackTrace();
	} finally{
		if (logger.isInfoEnabled()) {
	        logger.info("Opened " + flowComponent.toString());
	      }
	}
	      
	    
	  return flowComponent;
  }
  
  protected void closeOpenObject(FlowComponent object)
    throws Exception
  {
    UUID id = object.getMetaID();
    String uri = object.getMetaUri();
    try
    {
      object.close();
    }
    finally
    {
      if (logger.isInfoEnabled()) {
        logger.info("Closed " + uri);
      }
      this.metadataRepository.removeDeploymentInfo(Pair.make(id, getServerID()), WASecurityManager.TOKEN);
    }
  }
  
  long prevNumLogErrors = -1L;
  long prevNumLogWarns = -1L;
  long prevTimeStamp = 0L;
  Long prevCpuTime = null;
  private Logger monitorLogger;
  private MonitorLogAppender monitorAppender;
  // 获得监控器事件
  public Collection<MonitorEvent> getMonitorEvents(long ts)
  {
    Collection<MonitorEvent> monEvs = new ArrayList();
    
    UUID serverID = getServerID();
    UUID entityID = serverID;
    long timeStamp = ts;
    // 总内存, 最大内存, 剩余内存
    long totalMemory = Runtime.getRuntime().totalMemory();
    long maxMemory = Runtime.getRuntime().maxMemory();
    long freeMemory = maxMemory - (totalMemory - Runtime.getRuntime().freeMemory());
    // 添加监控器事件
    monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.UPTIME, Long.valueOf(timeStamp - startupTimeStamp), Long.valueOf(timeStamp)));
    monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.MEMORY_TOTAL, Long.valueOf(totalMemory), Long.valueOf(timeStamp)));
    monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.MEMORY_MAX, Long.valueOf(maxMemory), Long.valueOf(timeStamp)));
    monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.MEMORY_FREE, Long.valueOf(freeMemory), Long.valueOf(timeStamp)));
    try
    {
      java.lang.management.OperatingSystemMXBean mx = ManagementFactory.getOperatingSystemMXBean();
      com.sun.management.OperatingSystemMXBean mxb = (com.sun.management.OperatingSystemMXBean)mx;
      long cpuTime = mxb.getProcessCpuTime();
      if (cpuTime < 0L)
      {
        monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CPU_RATE, "Unavailable", Long.valueOf(timeStamp)));
      }
      else if (this.prevCpuTime != null)
      {
        long cpuDelta = Math.abs(cpuTime - this.prevCpuTime.longValue());
        int cores = Runtime.getRuntime().availableProcessors();
        Long cpuRate = Long.valueOf(1000L * cpuDelta / (timeStamp - this.prevTimeStamp));
        monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CPU_RATE, cpuRate, Long.valueOf(timeStamp)));
        String cpuRateRatePerNode = String.format("%2.1f%%", new Object[] { Double.valueOf(cpuRate.longValue() / 1.0E9D / cores * 100.0D) });
        monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CPU_PER_NODE, cpuRateRatePerNode, Long.valueOf(timeStamp)));
      }
      this.prevCpuTime = Long.valueOf(cpuTime);
    }
    catch (UnsupportedOperationException e)
    {
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CPU_RATE, "Not supported", Long.valueOf(timeStamp)));
    }
    catch (Exception e)
    {
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CPU_RATE, "Unavailable", Long.valueOf(timeStamp)));
    }
    try
    {
      File[] roots = File.listRoots();
      StringBuilder sb = new StringBuilder();
      for (File root : roots)
      {
        sb.append(root.toString()).append(": ").append(root.getFreeSpace() / 1000000000L).append("GB");
        if (root != roots[(roots.length - 1)]) {
          sb.append("; ");
        }
      }
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.DISK_FREE, sb.toString(), Long.valueOf(timeStamp)));
    }
    catch (UnsupportedOperationException e)
    {
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CPU_RATE, "Not supported", Long.valueOf(timeStamp)));
    }
    catch (Exception e)
    {
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CPU_RATE, "Unavailable", Long.valueOf(timeStamp)));
    }
    long timestamp;
    try
    {
      timestamp = System.currentTimeMillis();
      List<WActionStoreManager> instances = WActionStores.getAllElasticsearchInstances();
      for (WActionStoreManager instance : instances)
      {
        Client client = instance.getClient();
        NodesStatsResponse response = (NodesStatsResponse)client.admin().cluster().prepareNodesStats(new String[0]).setTransport(true).get();
        if (((NodeStats[])response.getNodes()).length > 0)
        {
          long txSum = 0L;
          long rxSum = 0L;
          for (NodeStats n : (NodeStats[])response.getNodes())
          {
            TransportStats o = n.getTransport();
            txSum += o.getTxSize().getBytes();
            rxSum += o.getRxSize().getBytes();
          }
          monEvs.add(new MonitorEvent(serverID, MonitorModel.ES_ENTITY_UUID, MonitorEvent.Type.ES_TX_BYTES, Long.valueOf(txSum), Long.valueOf(timestamp)));
          monEvs.add(new MonitorEvent(serverID, MonitorModel.ES_ENTITY_UUID, MonitorEvent.Type.ES_RX_BYTES, Long.valueOf(rxSum), Long.valueOf(timestamp)));
        }
      }
    }
    catch (Exception e)
    {
      
      Logger.getLogger("Monitor").error("ElasticsearchMonitor could not send batch to MonitorModel", e);
    }
    int cores = Runtime.getRuntime().availableProcessors();
    monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CORES, Long.valueOf(cores), Long.valueOf(timeStamp)));
    monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.VERSION, Version.getVersionString(), Long.valueOf(timeStamp)));
    if ((this instanceof Server))
    {
      Server server = (Server)this;
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.CLUSTER_NAME, server.ServerInfo.getInitializer().WAClusterName, Long.valueOf(timeStamp)));
    }
    LoggingEvent monLoggingEvent = null;
    long numLogErrors = 0L;
    long numLogWarns = 0L;
    while (((monLoggingEvent = (LoggingEvent)this.monitorLoggingEvents.peek()) != null) && 
      (monLoggingEvent.timeStamp <= ts))
    {
      try
      {
        monLoggingEvent = (LoggingEvent)this.monitorLoggingEvents.take();
      }
      catch (InterruptedException e) {}
      String logValue = monLoggingEvent.getLoggerName() + ": ";
      if (monLoggingEvent.locationInformationExists()) {
        logValue = logValue + monLoggingEvent.getLocationInformation().fullInfo + ": ";
      }
      logValue = logValue + monLoggingEvent.getMessage();
      if (logValue.length() > 4096) {
        logValue = logValue.substring(0, 4092) + "...";
      }
      MonitorEvent.Type logType;
      if (monLoggingEvent.getLevel().toInt() >= Level.ERROR.toInt())
      {
        logType = MonitorEvent.Type.LOG_ERROR;
        numLogErrors += 1L;
      }
      else
      {
        logType = MonitorEvent.Type.LOG_WARN;
        numLogWarns += 1L;
      }
      monEvs.add(new MonitorEvent(serverID, entityID, logType, logValue, Long.valueOf(timeStamp)));
    }
    if ((numLogErrors > 0L) || (this.prevNumLogErrors != 0L))
    {
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.NUM_LOG_ERRORS, Long.valueOf(numLogErrors), Long.valueOf(timeStamp)));
      this.prevNumLogErrors = numLogErrors;
    }
    if ((numLogWarns > 0L) || (this.prevNumLogWarns != 0L))
    {
      monEvs.add(new MonitorEvent(serverID, entityID, MonitorEvent.Type.NUM_LOG_WARNS, Long.valueOf(numLogWarns), Long.valueOf(timeStamp)));
      this.prevNumLogWarns = numLogWarns;
    }
    this.prevTimeStamp = timeStamp;
    
    return monEvs;
  }
  
  private BlockingQueue<LoggingEvent> monitorLoggingEvents = new MpscCompoundQueue(1024);
  public abstract boolean isServer();
  
  protected static abstract interface StreamObjectFac
  {
    public abstract FlowComponent create()
      throws Exception;
  }
  
  private static class MonitorLogAppender
    extends AppenderSkeleton
  {
    private final BaseServer parent;
    
    MonitorLogAppender(BaseServer parent)
    {
      this.parent = parent;
    }
    
    protected void append(LoggingEvent loggingEvent)
    {
      try
      {
        this.parent.monitorLoggingEvents.add(loggingEvent);
      }
      catch (IllegalStateException ignored) {}
    }
    
    public void close() {}
    
    public boolean requiresLayout()
    {
      return false;
    }
  }
  // 生成服务器名称
  private static String generateServerName()
    throws Exception
  {
    HazelcastInstance hz = HazelcastSingleton.get();
    ISemaphore lock = hz.getSemaphore("bloom Node Name Generator Semaphore");
    lock.init(1);
    lock.acquire();
    try
    {
      String setName = System.getProperty("com.bloom.config.server.name");
      if ((setName != null) && (!setName.isEmpty()))
      {
        if (serverNameIsInUse(hz, setName)) {
          throw new Exception("Server name already in use: " + setName);
        }
        if (setName.contains(".")) {
          throw new Exception("Server name must not contain a period: " + setName);
        }
        return setName;
      }
      InetSocketAddress adr = (InetSocketAddress)HazelcastSingleton.get().getLocalEndpoint().getSocketAddress();
      String address = adr.getAddress().getHostAddress().toString().replace('.', '-');
      String groupsString = System.getProperty("com.bloom.deploymentGroups");
      String serverOrAgent = HazelcastSingleton.isClientMember() ? "A" : "S";
      
      String[] groups = groupsString == null ? new String[0] : groupsString.split(",");
      Arrays.sort(groups);
      
      StringBuilder sb = new StringBuilder();
      sb.append(serverOrAgent);
      sb.append(address);
      String group;
      for (int i=0; i< groups.length; i++) {
    	  	group = groups[i];
        if (!group.equalsIgnoreCase("default"))
        {
          sb.append("_");
          sb.append(group.trim());
        }
      }
      String baseName = sb.toString().replaceAll("[^A-Za-z0-9]+", "_");
      if (!serverNameIsInUse(hz, baseName)) {
        return baseName;
      }
      baseName = baseName + "_";
      for (int i = 2; i <= 99; i++)
      {
        String fullName = baseName + i;
        if (!serverNameIsInUse(hz, fullName)) {
          return fullName;
        }
      }
      throw new Exception("Unable to generate a unique name similar to " + baseName + "1");
    }
    finally
    {
      lock.release();
    }
  }
  // 查看服务器名称是否正在使用
  public static boolean serverNameIsInUse(HazelcastInstance hz, String serverName)
  {
    Set<Member> allServersAndAgents = hz.getCluster().getMembers();
    RemoteCall<String> getServerNameCallable = new RemoteCall()
    {
      private static final long serialVersionUID = 4152339478021968346L;
      
      public String call()
      {
        return BaseServer.getServerName();
      }
    };
    Collection<String> namesInUse = DistributedExecutionManager.exec(hz, getServerNameCallable, allServersAndAgents);
    for (String nameInUse : namesInUse) {
      if (serverName.equals(nameInUse)) {
        return true;
      }
    }
    return false;
  }
  
  public static String getServerName()
  {
    return serverName;
  }
  // 创建广播的管道
  public Channel createChannel(FlowComponent owner)
  {
    return new BroadcastAsyncChannel(owner);
  }
  // 获得所有对象的视图
  public abstract Collection<FlowComponent> getAllObjectsView();
  
  public abstract Collection<Channel> getAllChannelsView();
  
  public abstract Stream getStream(UUID paramUUID, Flow paramFlow)
    throws Exception;
  
  public abstract ExecutorService getThreadPool();
  
  public abstract boolean inDeploymentGroup(UUID paramUUID);
  
  public abstract UUID getServerID();
  
  public abstract List<String> getDeploymentGroups();
  
  public abstract <T extends MetaInfo.MetaObject> T getObjectInfo(UUID paramUUID, EntityType paramEntityType)
    throws ServerException, MetaDataRepositoryException;
  // 获得分布式分发消息的管道, 为ZMQChannel
  public abstract ZMQChannel getDistributedChannel(Stream paramStream);
}
