#增量更新的WAEvent介绍

<p>
	使用 CDC 进行增量更新源端数据的输出数据类型是 WAEvent.此域和有效值在各类 Readers 中不尽相同,但是都包含了如下的组成部分： 
	metadata:
	-------------
	* OperationName: INSERT, UPDATE, 或 DELETE
	* TxnID: transaction ID
	* TimeStamp: 从 CDC 日志中获取的时间戳
	* TableName: 操作目标表的全名
</p>

<p>
	
</p>