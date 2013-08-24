#!/bin/bash
#download hadoop+hbase, create folders
apt-get update
location='https://s3-eu-west-1.amazonaws.com/BUCKET_NAME/tar'
hadoop_version='1.0.4'
hbase_version='0.94.6'
wget --no-check-certificate "$location/hadoop-$hadoop_version.tar.gz"
wget --no-check-certificate "$location/hbase-$hbase_version.tar.gz"
#unpack hadoop+hbase
mkdir /home/ubuntu/{hbase,hadoop}
sudo chown ubuntu /home/ubuntu/hbase
sudo chown ubuntu /home/ubuntu/hadoop
tar -xzf hadoop-$hadoop_version.tar.gz -C /home/ubuntu/hadoop
tar -xzf hbase-$hbase_version.tar.gz -C /home/ubuntu/hbase
rm hadoop-$hadoop_version.tar.gz
rm hbase-$hbase_version.tar.gz
#deprecated?: export HADOOP_HOME=/home/ubuntu/hadoop/hadoop-$hadoop_version
export HADOOP_PREFIX=/home/ubuntu/hadoop/hadoop-$hadoop_version
export HBASE_HOME=/home/ubuntu/hbase/hbase-$hbase_version
sudo mkdir -p /mnt/hadoop/dfs/data
#mkdir -p /home/ubuntu/hadoop/var/dfs/data
mkdir -p /home/ubuntu/hbase/var
sudo chown -R ubuntu /mnt/hadoop
sudo chown -R ubuntu /home/ubuntu


## OS SETUP ##
# Avoid OS security limits to become a scalability bottleneck.
#sudo rm /etc/security/limits.conf
cat >limits.conf <<END_OF_FILE
* soft nofile 32768
* hard nofile 32768
root soft nofile 32768
root hard nofile 32768
* soft memlock unlimited
* hard memlock unlimited
root soft memlock unlimited
root hard memlock unlimited
* soft as unlimited
* hard as unlimited
root soft as unlimited
root hard as unlimited
END_OF_FILE
sudo mv limits.conf /etc/security/limits.conf
sudo chown root:root /etc/security/limits.conf
sudo chmod 755 /etc/security/limits.conf
# Disable swap
sudo swapoff --all
echo "" > /home/ubuntu/setup.fin
