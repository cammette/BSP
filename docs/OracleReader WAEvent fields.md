#OracleReader WAEvent fields

<p>
	对于OracleReader, 输出类型是WAEvent. 此域是: <br />
	metadata: 最常用的域是: <br />
	
	<ol>
		<li>OperationName: COMMIT, BEGIN, INSERT, DELETE, UPDATE, 或 (当仅使用 LogMiner) ROLLBACK.</li>
		<li>TxnID: transaction ID.</li>
		<li>TableName (仅为 INSERT, DELETE, 和 UPDATE 操作返回): 表的全名</li>
		<li>ROWID (仅为 INSERT, DELETE, 和 UPDATE 操作): 对于inserted, deleted, 或 updated 行的 Oracle ID.</li>
		
	</ol>
	<br />
	为了查询这些域的值, 使用 META 函数.查看 为CDC readers解析WAEvent的数据域.

</p>

<p>
data: 一个域的数组, 从0计数, 包括:	

	<br />
	<ol>
		<li>对于一个 INSERT 或 DELETE 操作, 这个值是插入或者删除</li>
		<li>对于一个 UPDATE, 在操作完成之后的值</li>
		<li><a>GoldenGateTrailParser 示例应用和输出 </a></li>
	
	</ol>
	<br />

	为了查询这些域的值, 使用 SELECT ... (DATA[]).查看 为CDC readers解析WAEvent的数据域.

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
        <p  >元数据属性    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  > 当使用LogMiner时    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  > 当使用XStream Out时    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >说明    </p>
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
        <p  >Audit session ID 与 User Session 相关联, 更新数据    </p>
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
System change number (SCN), 当事务提交时的SCN值    </span>
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
        <p  >用户层 SQL 操作 (INSERT, UPDATE, ...) , 用于更新   </p>
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
        <p  >父事务标识符的裸代表    </p>
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
        <p  >true 如果一个 UPDATE 操作更改了主键, 否则为 false    </p>
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
        <p  >在日志文件中的 RBA 块的号码    </p>
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
        <p  >Sequence# 与 redo record 的Redo Block Address (RBA) 相关联, 此 redo record与更新有关    </p>
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
        <p  >唯一标识 redo record , 生成此行. Tuple (RS_ID, SSN)  组合在一起唯一标识一个逻辑行的变更.    </p>
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
        <p  >1 = 如果 redo record 生成了, 由于相关的事务半回滚或完全回滚, 0 = 反之    </p>
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
        <p  >通过更新, 修改行的Row ID (如果更新仅针对一个DML操作, 有意义). 将要是NULL, 如果redo record并不与一个 DML相互关联.    </p>
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
        <p  >System change number (SCN) 当数据库已经更新时    </p>
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
        <p  >修改的数据分段的名称    </p>
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
        <p  >修改的数据分段的类型 (INDEX, TABLE, ...)    </p>
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
        <p  >做更新的 Session 的序列号   </p>
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
        <p  >做更新的 Session 的序列号    </p>
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
        <p  >
        	数据库 Session 执行事务的信息.包含进程信息, 用户登录的机器名称, 客户端信息, 等等
           </p>
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
        <p  >重新构造的SQL语句长度, 等同于原有的SQL语句.    </p>
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
        <p  >修改表的名称 (redo 从属于一个表的更新)    </p>
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
        <p  >表空间的名称,包含了已经修改的数据分段.    </p>
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
        <p  >更新数据库的线程ID    </p>
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
        <p  >更新数据库的时间戳    </p>
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
        <p  >进行更新的事务名称; 仅对事务是一个命名事务时有意义    </p>
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
        <p  >事务标识符的原生表示    </p>
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

