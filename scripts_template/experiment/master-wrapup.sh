#!/bin/bash
if [ "$#" -lt 1 ]; then
	echo "Usage: $0 <experiment_name>"
	exit 1
fi
experiment_name=$1
#s3cmd --configure
s3cmd put -r hbase/hbase-0.94.6/logs s3://BUCKET_NAME/results/$experiment_name/
