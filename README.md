# Mirai_Plugins_Auto_Reply

#### Mirai控制台插件

**自动回复插件**

第一次启动后 在./conf/auto_reply下配置文件:

### **_注意 # 开头的行内容不会被读取_**

### **_请令起一行或删除全部内容 后输入_**

**_host : 你的qq_**

_**key : 开始添加词的触发**_

_**selectKey : 查询词的触发**_

_**deleteKey : 删除词的触发**_

_**OneComAddSplit : 一次命令分隔符**_

_**OneComAddStr : 一次命令触发词,默认:/添加**_

_**followers : 可以添加和查询的人员**_

_**data.data :数据文件**_

========update Time On 10/11========

_**openPrivate : 开启私聊**_

_**illegalKeys: 不允许添加的词 (敏感词汇 )**_

在群里发:

    开始添加   #开始添加词汇

    删除词     #删除某个已添加的词

    查询词     #删除某个已添加的词

也可以,用一次命令添加 例如:

    /添加 你好 你好啊

特别的支持模糊词:

    %   代表单个未知字符

    %+  代表一到多个未知字符
    
    %?  代表0到多个未知字符

####特别的 可以使用 "设置冷却 1"  来设置冷却 单位:秒 或在配置文件(cd)中写入值后重启