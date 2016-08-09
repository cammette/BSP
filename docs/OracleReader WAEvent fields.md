#OracleReader WAEvent fields

<p>
	对于OracleReader, 输出类型是WAEvent. 此域是: <br />
	metadata: 最常用的域是: <br />
	
	<ol>
		<li>OperationName: COMMIT, BEGIN, INSERT, DELETE, UPDATE, 或 (当仅使用 LogMiner) ROLLBACK.</li>
		<li>TxnID: transaction ID.</li>
		<li>TableName (returned only for INSERT, DELETE, and UPDATE operations): 表的全名</li>
		<li>ROWID (returned only for INSERT, DELETE, and UPDATE operations): 对于inserted, deleted, or updated 行的 Oracle ID.</li>
		
	</ol>
	<br />
	为了查询这些域的值, 使用 META 函数.查看 Parsing the fields of WAEvent for CDC readers.

</p>

<p>
data: 一个域的数组, 从0计数, 包括:	

	<br />
	<ol>
		<li>for an INSERT or DELETE operation, the values that were inserted or deleted</li>
		<li>for an UPDATE, the values after the operation was completed</li>
		<li><a>GoldenGateTrailParser sample application and output</a></li>
	
	</ol>
	<br />

	为了查询这些域的值, 使用 SELECT ... (DATA[]).查看  Parsing the fields of WAEvent for CDC readers.

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

	before (仅用于 UPDATE 操作): 与数据的格式相同, 但是包含了在 UPDATE操作之前的值.
	<br />

	dataPresenceBitMap, beforePresenceBitMap, 和 typeUUID 是预留的, 应该被忽略.

</p>






<p>
如下是可能出现在元数据中域的完全列表. 实际的域可能依赖于操作类型和其他的因素.<br />

<table class="confluenceTable">
        <thead class=" ">    <tr>
            <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >metadata property    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >present<br/>when using<br/>LogMiner    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >present<br/>when using<br/>XStream Out    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >comments    </p>
            </td>
        </tr>
</thead><tfoot class=" "></tfoot><tbody class=" ">    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >AuditSessionID    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Audit session ID associated with the user session making the change    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >BytesProcessed    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >COMMITSCN    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >    <span style="color: #000000;">
      <span style="color: #222222;">
System change number (SCN) when the transaction committed    </span>
      </span>
    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Operationname    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >User level SQL operation that made the change (INSERT, UPDATE, ...)    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >ParentTxnID    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Raw representation of the parent transaction identifier    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >PK_UPDATE    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >true if an UPDATE operation changed the primary key, otherwise false    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >RbaBlk    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >RBA block number within the log file    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >RbaSqn    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Sequence# associated with the Redo Block Address (RBA) of the redo record associated with the change    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >RecordSetID    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Uniquely identifies the redo record that generated the row. The tuple (RS_ID, SSN) together uniquely identifies a logical row change.    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >RollBack    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >1 = if the redo record was generated because of a partial or a full rollback of the associated transaction, 0 = otherwise    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >ROWID    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Row ID of the row modified by the change (only meaningful if the change pertains to a DML). This will be NULL if the redo record is not associated with a DML.    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >SCN    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >System change number (SCN) when the database change was made    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >SegmentName    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Name of the modified data segment    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >SegmentType    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Type of the modified data segment (INDEX, TABLE, ...)    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Serial    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Serial number of the session that made the change    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Session    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Session number of the session that made the change    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >SessionInfo    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Information about the database session that executed the transaction. Contains process information, machine name from which the user logged in, client info, and so on.    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >SQLRedoLength    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Length of reconstructed SQL statement that is equivalent to the original SQL statement that made the change.    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >TableName    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Name of the modified table (in case the redo pertains to a table modification)    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >TableSpace    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Name of the tablespace containing the modified data segment.    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >ThreadID    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >ID of the thread that made the change to the database    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >TimeStamp    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Timestamp when the database change was made    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >TransactionName    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Name of the transaction that made the change; only meaningful if the transaction is a named transaction    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >TxnID    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Raw representation of the transaction identifier    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >TxnUserID    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >x    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
        </tr>
</tbody>        </table>



</p>

