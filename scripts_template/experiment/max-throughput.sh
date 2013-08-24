#!/bin/bash
number_of_requests=10000000

if [ $# -lt 4 ]; then
echo "Usage: $0 <series-name> <workload_file> <threads> <duration>"
exit 1
fi

mkdir -p /mnt/experiment-data/$1
workload_file=$2
storage_file="maxtp-$2-$3.dat"
cp -f $workload_file /mnt/experiment-data/$1/
#nohup /home/ubuntu/collect-data.sh $1 $4 &
echo "starting_point="$(date +%s) >> /mnt/experiment-data/$1/$storage_file
echo "workload=$2" >> /mnt/experiment-data/$1/$storage_file
echo "threads=$3" >> /mnt/experiment-data/$1/$storage_file
nohup /home/ubuntu/ycsb/ycsb-0.1.4/bin/ycsb run hbase -P $workload_file -p operationcount=$number_of_requests -p maxexecutiontime=$4 -threads $3 -s >> /mnt/experiment-data/$1/$storage_file &
exit
