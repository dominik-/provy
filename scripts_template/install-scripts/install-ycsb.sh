#!/bin/bash
location='https://s3-eu-west-1.amazonaws.com/BUCKET_NAME/tar'
version="0.1.4"
target='/home/ubuntu/ycsb'
hbase_location='/home/ubuntu/hbase'
hbase_version='0.94.6'
zookeeper_version='3.4.5'

echo "Installing YCSB $version..."
wget --no-check-certificate "$location/ycsb-$version.tar.gz"
mkdir $target
sudo chown ubuntu $target
tar -xzf ycsb-$version.tar.gz -C $target
rm ycsb-$version.tar.gz
