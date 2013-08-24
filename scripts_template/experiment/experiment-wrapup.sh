#!/bin/bash
if [ "$#" -lt 1 ]; then
	echo "Usage: $0 <experiment_name>"
	exit 1
fi
experiment_name=$1
#s3cmd --configure
cp -r configs /mnt/experiment-data/$experiment_name/
s3cmd put -r /mnt/experiment-data/$experiment_name s3://BUCKET_NAME/results/
