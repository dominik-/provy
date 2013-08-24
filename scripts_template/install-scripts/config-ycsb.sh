#!/bin/bash
#requires configured hadoop/hbase!
version="0.1.4"
target='/home/ubuntu/ycsb'
hbase_location='/home/ubuntu/hbase'
hbase_version='0.94.6'
zookeeper_version='3.4.5'

echo "Configuring YCSB $version..."
while [ ! -f config.fin ]; do
echo "waiting for configuration to complete.."
sleep 1
done
if [ -f $target/ycsb-$version/hbase-binding/conf/hbase-site.xml ]
then
	rm -f $target/ycsb-$version/hbase-binding/conf/hbase-site.xml
fi
cp $hbase_location/hbase-$hbase_version/conf/hbase-site.xml $target/ycsb-$version/hbase-binding/conf/hbase-site.xml
echo "Updating HBase and Zookeeper jars.."
cp $hbase_location/hbase-$hbase_version/hbase-$hbase_version.jar $target/ycsb-$version/hbase-binding/lib/
cp $hbase_location/hbase-$hbase_version/lib/zookeeper-$zookeeper_version.jar $target/ycsb-$version/hbase-binding/lib/
echo "Finished configuring YCSB."
