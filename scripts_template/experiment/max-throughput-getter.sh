#!/bin/bash
if [ $# -lt 1 ]; then
echo "Usage: $0 <experiment_name>"
exit 1
fi
throughputfiles=$(ls "/mnt/experiment-data/$1/" | grep "^maxtp")

for file in $throughputfiles
do
echo $(head -n 3 /mnt/experiment-data/$1/$file) >> "/mnt/experiment-data/$1/throughput-results.log"
awk '/OVERALL/ {print}' /mnt/experiment-data/$1/$file >> "/mnt/experiment-data/$1/throughput-results.log"
awk '/PercentileLatency/ {print}' /mnt/experiment-data/$1/$file >> "/mnt/experiment-data/$1/throughput-results.log"
done
