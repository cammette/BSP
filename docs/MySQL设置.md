#MySQL 设置

<p>
	在使用 MySQL 适配器之前, 一个 MySQL 管理员必须创建一个用户给适配器, 并且分配它必要的权限:
: <br />
	
	
	<pre><code>
        CREATE USER 'wauser' IDENTIFIED BY '******';
        GRANT REPLICATION SLAVE ON *.* TO 'wauser';
        GRANT REPLICATION CLIENT ON *.* TO 'wauser';
        GRANT SELECT ON *.* TO 'wauser';
    </code></pre>
	
	<br />
    <ol>
        <li>REPLICATION 权限 必须授权在 *.*. 这是MySQL的一个限制.</li>
        <li>你可能使用任何其他的有效名称, 来代替wauser.</li>
        <li>使用一个安全密码来替换 ****** .</li>
        <li>你可能让 SELECT 语句来仅允许访问这些表, 通过你的应用. 在这种情况下, 如果其他的表在MySQLReader属性中被指定, 平台将返回一个错误,说明它们不存在.</li>
            
    
    </ol>

    <br />




</p>

<p>
 MySQLReader 从 MySQL Binlog 中读取数据. 如果你的MySQL 服务器没有使用复制, binary log 可能是禁用的, 在这种情况下, 平台尝试读取可能是失败的, 有如下错误显示:

	<br />
	   <pre><code>
        2016-04-25 19:05:40,377 @ -WARN hz._hzInstance_1_striim351_0423.cached.thread-2 com.webaction.runtime.Server.startSources (Server.java:2477) Failure in Starting Sources.
java.lang.Exception: Problem with the configuration of MySQL
Row logging must be specified.
Binary logging is not enabled.
The server ID must be specified.
Add --binlog-format=ROW to the mysqld command line or add binlog-format=ROW to your my.cnf file
Add --bin-log to the mysqld command line or add bin-log to your my.cnf file
Add --server-id=n where n is a positive number to the mysqld command line or add server-id=n to your my.cnf file
        at com.webaction.proc.MySQLReader_1_0.checkMySQLConfig(MySQLReader_1_0.java:605) ...
       </code></pre>
	<br />

为了解决上述问题, 请查看MySQL对应版本的文档, 启动这个 binary log.

</p>







