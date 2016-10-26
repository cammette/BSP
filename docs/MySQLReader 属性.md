#MySQLReader 属性

<p>
	在使用 MySQL 适配器之前, 得完成对本平台的安装和配置, 同时安装MySQL JDBC Driver:
	
	
	
	<br />
   

   <table class="confluenceTable">
        <thead class=" ">    <tr>
            <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >属性    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >类型    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >默认值    </p>
            </td>
                <td  class="confluenceTh" rowspan="1" colspan="1">
        <p  >记录    </p>
            </td>
        </tr>
</thead><tfoot class=" "></tfoot><tbody class=" ">    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Tables    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >java.lang.String    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >此表返回更新数据. 名称是大小写敏感的. 如果一个值并不指定数据库, 你必须使用表的全名. 你可能指定多个表作为一个列表, 或者使用如下的掩码:    </p>
<ul class=" "><li class=" ">    <p  ><tt class=" ">%</tt>: 任何字符串    </p>
</li><li class=" ">    <p  ><tt class=" ">_</tt>: 任何单个字符    </p>
</li></ul>    <p  >例如, <tt class=" ">my.%</tt> 可能包含所有表 <tt class=" ">在你的数据库中</tt>.    </p>
    <p  >如果任意指定的表是缺失的, MySQLReader 将要发布一个警告. 如果没有指定的表存在, 启动将失败 &quot;带有 "found no tables" &quot; 错误.    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Username    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >java.lang.String    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >用户创建的登录名 在 <a   href="MySQL_setup.html">MySQL 设置</a>    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Password    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >com.webaction.security.Password    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >为此用户名指定的密码 (查看 <a   href="Encrypted_passwords.html">已加密的密码</a>)    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >ConnectionURL    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >java.lang.String    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  ><tt class=" ">mysql://</tt> MySQL 服务器的 IP 地址或网络名称, 冒号和端口号 (如果没有被指定, 端口号为 3306), 和数据库名.    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >SendBeforeImage    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >java.lang.Boolean    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >True    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >设置为 False  <tt class=" ">在</tt> 数据输出之前 省略   </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >Database    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >java.lang.String    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >包含表的数据库名称 (may be omitted 如果在 <tt class=" ">Tables</tt> 或 <tt class=" ">ConnectionURL</tt> 中指定，则可以忽略)    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >FilterTransactionBoundaries    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >java.lang.Boolean    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >True    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >默认值为 True, 开始(begin) 和 提交(commit) 事务被过滤掉. 设置为False 将包含开始(begin) 和 提交(commit).    </p>
            </td>
        </tr>
    <tr>
            <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >ExcludedTables    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >java.lang.String    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >&nbsp;    </p>
            </td>
                <td  class="confluenceTd" rowspan="1" colspan="1">
        <p  >指定表的增量更新数据被排除. 例如, 如果 <tt class=" ">Tables</tt> 使用了一个掩码, 来自指定表的数据将被忽略. 可以使用多个表名和掩码来指定 <tt class=" ">Tables</tt> 表.    </p>
            </td>
        </tr>
</tbody>        </table>

    <br />




</p>
