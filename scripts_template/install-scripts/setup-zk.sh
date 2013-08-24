#!/bin/bash
#run as root
#requires unzipped config files!
zookeeper_version='3.4.5'
location='https://s3-eu-west-1.amazonaws.com/BUCKET_NAME/tar'
wget --no-check-certificate "$location/zookeeper-$zookeeper_version.tar.gz"
mkdir -p /home/ubuntu/zookeeper/data
tar -xzf zookeeper-$zookeeper_version.tar.gz -C /home/ubuntu/zookeeper
chown -R ubuntu /home/ubuntu/zookeeper/
cp -f /home/ubuntu/configs/zookeeper/zoo.cfg /home/ubuntu/zookeeper/zookeeper-$zookeeper_version/conf/
/home/ubuntu/zookeeper/zookeeper-$zookeeper_version/bin/zkServer.sh start

