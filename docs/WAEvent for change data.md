#增量更新的WAEvent介绍

<p>
	使用 CDC 进行增量更新源端数据的输出数据类型是 WAEvent.此域和有效值在各类 Readers 中不尽相同,但是都包含了如下的组成部分： 
	<br />
	metadata:
	<br />
	<ol>
		<li>OperationName: INSERT, UPDATE, 或 DELETE</li>
		<li>TxnID: transaction ID</li>
		<li>TimeStamp: 从 CDC 日志中获取的时间戳</li>
		<li>TableName: 操作目标表的全名</li>
		
	</ol>
	<br />
	为了查询这些域的值, 请使用 META 函数. 查看 <a>Parsing the fields of WAEvent for CDC readers</a>. 
	<br />
	data: field域的数组, 从0开始计数, 包含如下:
	<ol>
		<li>对于INSERT 或 DELETE操作, 此值是被插入或删除的值</li>
		<li>对于UPDATE操作, 此值是操作完成之后的值</li>
	<ol>
	<br />
	为了查询这些域的值, 请使用 SELECT ... (DATA[])语句. 查看 <a>Parsing the fields of WAEvent for CDC readers</a>
	<br />
	before (仅UPDATE操作使用): 与数据相同格式, 但是包含了UPDATE操作之前的值
	<br />
	dataPresenceBitMap, beforePresenceBitMap , 和 typeUUID 是保留的, 应该被忽略.

</p>

<p>
	对于额外的域和值的详细信息, 请查看:
	<a>MSSQLReader WAEvent fields</a>
	<a>OracleReader WAEvent fields</a>
	
</p>