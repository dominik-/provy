#!/bin/bash
config='https://BUCKET_NAME.s3-eu-west-1.amazonaws.com'
hadoop_location='/home/ubuntu/hadoop'
hadoop_version='1.0.4'
hbase_location='/home/ubuntu/hbase'
hbase_version='0.94.6'

wget --no-check-certificate https://BUCKET_NAME.s3-eu-west-1.amazonaws.com/scripts/install-scripts/config-hadoop.sh
chmod +x config-hadoop.sh

bash config-hadoop.sh
wait
bash hadoop/hadoop-$hadoop_version/bin/hadoop-daemon.sh start datanode
wait
bash hbase/hbase-$hbase_version/bin/hbase-daemon.sh start regionserver

