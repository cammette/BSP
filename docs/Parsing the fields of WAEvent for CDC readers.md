#Parsing the fields of WAEvent for CDC readers

<p>
	WAEvent 是一个数据类型, CDC Reader 输出流来使用此数据类型： 
	
	<br />
	<ol>
		<li>WAEvent的数据类型包含了一个事件的域值的数组.</li>
		<li>WAEvent的元域(meta field)包含了此事件的元数据的数组.</li>
		<li>TimeStamp: 从 CDC 日志中获取的时间戳</li>
		<li>对于更新操作, WAEvent的 'before' 域包含了之前域值的数组.</li>
		
	</ol>
	<br />
</p>

<p>
一个 WAEvent 流依赖于不同的 Reader. 如下章节有更详细的信息:	

	<br />
	<ol>
		<li><a>MSSQLReader WAEvent fields.</a></li>
		<li><a>OracleReader WAEvent fields.</a></li>
		<li><a>GoldenGateTrailParser sample application and output</a></li>
	
	</ol>
	<br />

	使用如下的函数用来解析一个 CDC Reader 的输出流.

</p>

<p>
###DATA[]

####DATA [field_number]

对于每一个事件, 从数据数组中返回特定域的值.<br />
	<ol>
		<li>此数组的第一个域是 0.</li>
		<li>在 SELECT 语句中的DATA函数的顺序决定了输出中域的顺序. 这些可能以任意的顺序被指定: 例如, data[1]可能在data[0]之前.</li>
	
	</ol>
	<br />



</p>



<p>
###IS_PRESENT()

####IS_PRESENT ( stream_name, [ before | data ], field_number )

对于每一个事件, 返回 true 或 false 依赖于是否 before 或 data array 有一个值,对于特定的域. 例如, 如果你执行如下的更新在一个 Oracle表上:
<br />
	<pre><code>
		UPDATE POSAUTHORIZATIONS SET BUSINESS_NAME = 'COMPANY 5A' where pos=0;
	</code></pre>
<br />

WAEvent对于这个更新看起来如下所示:<br />

<br />
	<pre><code>
		data: ["COMPANY 5A","D6RJPwyuLXoLqQRQcOcouJ26KGxJSf6hgbu","6705362103919221351","0","20130309113025","0916","USD","2.2","5150279519809946","41363","Quicksand"]
before: ["COMPANY 1",null,null,null,null,null,null,null,null,null,null]
	</code></pre>
<br />

你能够使用如下的代码来返回更新数据域的值, 同时对于其他域是 NOT_UPDATED:

<br />
	<pre><code>
		SELECT
  CASE WHEN IS_PRESENT(OracleCDCStream,before,0)==true THEN data[0].toString()
    ELSE "NOT_UPDATED"
  END,
  CASE WHEN IS_PRESENT(OracleCDCStream,before,1)==true THEN data[1].toString()
    ELSE "NOT_UPDATED"
  END ...
	</code></pre>
<br />


</p>


<p>
###META()

####META (stream_name, metadata_key)

对于每个事件, 对于特定的元数据键(metadata key)返回此值. Metadata keys 对于每个适配器是特定的.<br />
例如, META(OracleStream, TableName) 能够返回相关 Oracle 数据库的表名.<br />




</p>

