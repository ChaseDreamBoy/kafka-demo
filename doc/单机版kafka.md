# 一、下载解压

<pre><code>
# 下载
wget http://apache.claz.org/kafka/2.3.0/kafka_2.12-2.3.0.tgz
wget http://ftp.jaist.ac.jp/pub/apache/zookeeper/zookeeper-3.5.5/apache-zookeeper-3.5.5-bin.tar.gz

# 解压到当前目录的app文件夹下面
tar zxvf ./kafka_2.12-2.3.0.tgz -C ./app/
tar zxvf ./apache-zookeeper-3.5.5-bin.tar.gz -C ./app/
</code></pre>

# 二、检查是否安装jvm环境
<pre><code>
java -version

# ubuntu 上安装java  
# 解压jdk到app目录：
tar -zxvf ./jdk-8u172-linux-x64.tar.gz -C ../app

# 把jdk配置系统环境变量中： 
vim ~/.bashrc
# /usr/software/hadoop/app/jdk1.8.0_172 这是我的jdk解压目录
export JAVA_HOME=/usr/software/hadoop/app/jdk1.8.0_172
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar$JAVA_HOME/lib/tools.jar

# 使得配置修改生效：
source ~/.bashrc

验证：java -version 
</code></pre>

# 三、配置 zookeeper 
<pre><code>
# 先进入 zk 安装目录
cd ZK_HOME
cp ./conf/zoo_sample.cfg ./conf/zoo.cfg

vim ./conf/zoo.cfg
# 这个数据存储目录需要事先创建好
dataDir=/usr/software/kafka/data/zk
# 这里注意使用对应计算机ip  因为在java连接kafka时候回获取元数据，就会获取这里的ip
server.2=192.168.22.2:2888:3888

# 退出 vim  
# conf/zoo.cfg 中 dataDir 目录 下 新建 myid 文件  并指定  server id 
# server id 与 上面的 对应
echo 2 > /usr/software/kafka/data/zk/myid

# 启动 zk
./bin/zkServer.sh start
# 查看 zk 状况
./bin/zkServer.sh status
# 使用 java 的 jpl 查看运行的程序
jps -l
</code></pre>

# 四、配置 kafka
<pre><code>
# 进入kafka安装目录
cd KAFKA_HOME

vim ./config/server.properties
broker.id=1
listeners=PLAINTEXT://:9092
# 需要事先创建该目录
log.dirs=/usr/software/kafka/data/kafka
# 这里配置了 kafka的元数据存储在zk的kafka目录下 可以保证多个kafka集群使用同一个zk集群
# 对于是zk 集群的写法是： zookeeper.connect=ip1:2181,ip2:2181,ip3:2181/kafka  只需要在最后加一个kafka目录就好
# 由于这里配置了存储到zk下的kafka目录，不是根目录，在使用到命令中用到zk连接信息时需要把kafka目录带上，如下：
# ./bin/kafka-topics.sh --list --zookeeper 127.0.0.1:2181/kafka
zookeeper.connect=localhost:2181/kafka
# 不允许自动创建topic(比如producer创建topic)
auto.create.topics.enable=false
# 领导选举机制
unclean.leader.election.enable=false
# 不要自动 从平衡 consumer group 大对性能影响大，也可能会丢失数据
auto.leader.rebalance.enable=false


# 启动 kafka
./bin/kafka-server-start.sh ./config/server.properties &

# 后台启动 kafka
# 1>/dev/null 2>&1 是将命令产生的输入和错误都输入到空设备，也就是不输出的意思。/dev/null 代表空设备，“/”不要漏掉，否则会报错；
nohup ./bin/kafka-server-start.sh ./config/server.properties 1>/dev/null 2>&1 &
</code></pre>