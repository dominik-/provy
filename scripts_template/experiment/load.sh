#!/bin/bash
if [ $# -lt 2 ]; then
echo "Usage: $0 <series-name> <number_of_records>"
exit 1
fi
mkdir -p /mnt/experiment-data/$1
echo "starting_point="$(date +%s) > /mnt/experiment-data/$1/load.dat
/home/ubuntu/ycsb/ycsb-0.1.4/bin/ycsb load hbase -P workload-read-50.ycsb -p recordcount=$2 -s >> /mnt/experiment-data/$1/load.dat
exit
