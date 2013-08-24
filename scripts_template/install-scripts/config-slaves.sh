#!/bin/bash
for i in $(cat /home/ubuntu/hadoop/hadoop-1.0.4/conf/slaves); do
ssh-keyscan -H $i >> /home/ubuntu/.ssh/known_hosts
scp /home/ubuntu/hadoop/hadoop-1.0.4/conf/* ubuntu@$i:/home/ubuntu/hadoop/hadoop-1.0.4/conf
scp /home/ubuntu/hbase/hbase-0.94.6/conf/* ubuntu@$i:/home/ubuntu/hbase/hbase-0.94.6/conf
done
