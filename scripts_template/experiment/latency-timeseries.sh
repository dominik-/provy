#!/bin/bash
if [ $# -lt 6 ]; then
echo "Usage: $0 <series-name> <name> <workload_file> <runtime[seconds]> <requests_per_second> <threads>"
exit 1
fi
mkdir -p /mnt/experiment-data/$1
workload_file=$3
cp -f $workload_file /mnt/experiment-data/$1/
nohup /home/ubuntu/collect-data.sh $1 $4 &
number_of_requests=$(($4*$5))
echo "starting_point="$(date +%s) > /mnt/experiment-data/$1/$2.dat
nohup /home/ubuntu/ycsb/ycsb-0.1.4/bin/ycsb run hbase -P $workload_file -p operationcount=$number_of_requests -target $5 -p maxexecutiontime=$4 -threads $6 -p measurementtype=timeseries -p timeseries.granularity=1000 -s >> /mnt/experiment-data/$1/$2.dat &
exit
