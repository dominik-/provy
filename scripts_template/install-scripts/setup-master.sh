#!/bin/bash
#create namenode directories
mkdir /home/ubuntu/hadoop/var/dfs/{name,namesecondary}
#get private key to access user ubuntu on other machines
wget --no-check-certificate https://s3-eu-west-1.amazonaws.com/BUCKET_NAME/hbase.pem
cp hbase.pem /home/ubuntu/.ssh/id_rsa
chmod 600 /home/ubuntu/.ssh/id_rsa
chown ubuntu /home/ubuntu/.ssh/id_rsa
rm hbase.pem
chown -R ubuntu /home/ubuntu/hadoop
chown -R ubuntu /home/ubuntu/hbase
