#!/bin/bash
#all scripts to be run on the master node
wget --no-check-certificate https://BUCKET_NAME.s3-eu-west-1.amazonaws.com/scripts/install-scripts/config-hadoop.sh
chmod +x config-hadoop.sh
wget --no-check-certificate https://BUCKET_NAME.s3-eu-west-1.amazonaws.com/scripts/install-scripts/setup-zk.sh
chmod +x setup-zk.sh

for i in $(cat /home/ubuntu/hadoop/hadoop-1.0.4/conf/slaves); do
ssh-keyscan -H $i >> /home/ubuntu/.ssh/known_hosts
done
bash config-hadoop.sh
wait
bash setup-zk.sh
wait
echo "Y" | hadoop/hadoop-1.0.4/bin/hadoop namenode -format
wait
bash hadoop/hadoop-1.0.4/bin/start-dfs.sh
wait
bash hbase/hbase-0.94.6/bin/start-hbase.sh
