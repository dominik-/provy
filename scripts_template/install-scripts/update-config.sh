#!/bin/bash
config='https://BUCKET_NAME.s3-eu-west-1.amazonaws.com'
hadoop_location='/home/ubuntu/hadoop'
hadoop_version='1.0.4'
hbase_location='/home/ubuntu/hbase'
hbase_version='0.94.6'

rm configs.zip
wget --no-check-certificate $config/configs.zip
unzip -d configs -o configs.zip

echo "Updating configuration.."
cp -f configs/hadoop/* $hadoop_location/hadoop-$hadoop_version/conf/
cp -f configs/hbase/* $hbase_location/hbase-$hbase_version/conf/
