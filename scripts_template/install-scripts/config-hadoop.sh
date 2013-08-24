#!/bin/bash
config='https://BUCKET_NAME.s3-eu-west-1.amazonaws.com'
hadoop_location='/home/ubuntu/hadoop'
hadoop_version='1.0.4'
hbase_location='/home/ubuntu/hbase'
hbase_version='0.94.6'

wget --no-check-certificate $config/configs.zip
unzip -d configs configs.zip

#configure hadoop
while [ ! -f setup.fin ]; do
echo "waiting for installation to complete.."
sleep 1
done
echo "Configuring hadoop..."
if [ -d $hadoop_location/hadoop-$hadoop_version ]
	then
		cp -f configs/hadoop/* $hadoop_location/hadoop-$hadoop_version/conf/
	else
		echo "No hadoop installation found, skipping."
fi
echo "Configuring hbase..."
if [ -d $hbase_location/hbase-$hbase_version ]
	then
		cp -f configs/hbase/* $hbase_location/hbase-$hbase_version/conf/
	else
		echo "No hbase installation found, skipping."
fi
echo "Disabling strict SSH key verification..."
cp -f configs/ssh/config ~/.ssh/
echo "Configuration of hadoop and hbase complete."
echo "" > config.fin
