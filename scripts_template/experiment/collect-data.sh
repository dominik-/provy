#!/bin/bash

# Duration is measured in ticks. A tick discribes the number of intervals used for measurement
# total runtime = DURATION * INTERVAL

if [ "$#" -lt 2 ]
   then
      echo "Usage: $0 <experiment_name> <duration_seconds>"
      exit 1
fi

NAME="$1"
#EXPERIMENT="$2"
DURATION="$2"
#interval in [sec]. An interval of "150" corresponds to 2.5 minutes
INTERVAL=150
TICKS=$((($DURATION+$INTERVAL-1)/$INTERVAL))
CLUSTERNAME="hbase"
SOURCE_MONITORING_DATA_DIR="/var/lib/ganglia/rrds/"
TARGET_MONITORING_DATA_BASE_DIR="/mnt/experiment-data/"
SOURCE_MONITORING_DATA_DIR_CLUSTER="${SOURCE_MONITORING_DATA_DIR}${CLUSTERNAME}/"
NODES_FILES="/home/ubuntu/configs/internaldnslist"
METRICS="mem_free.rrd disk_free.rrd bytes_in.rrd bytes_out.rrd load_one.rrd load_five.rrd cpu_idle.rrd hbase.regionserver.stores.rrd hbase.regionserver.storefiles.rrd hbase.regionserver.regions.rrd hbase.regionserver.compactionQueueSize.rrd hbase.regionserver.requests.rrd hbase.regionserver.fsReadLatency_avg_time.rrd rpc.metrics.NumOpenConnections.rrd rpc.metrics.RpcQueueTime_avg_time.rrd rpc.metrics.RpcProccessingTime_avg_time.rrd"

mkdir -p ${TARGET_MONITORING_DATA_BASE_DIR}${NAME}
TARGET_MONITORING_DATA_DIR="${TARGET_MONITORING_DATA_BASE_DIR}${NAME}/"
log=${TARGET_MONITORING_DATA_DIR}collection_log.txt

tick_counter="0"

echo "Start collecting monitoring data for series ${NAME} and duration '${DURATION}' (${TICKS} ticks) to target directory ${TARGET_MONITORING_DATA_DIR}" | tee -a  $log
sleep $INTERVAL
while [ $tick_counter -lt $TICKS ]
do
	CLUSTER=""
	for file in $NODES_FILES; do
		for line in $(cat $file); do 
			CLUSTER="$CLUSTER $line"
		done
	done
	cur_tick_start_time=$(date +%s)
	next_tick_start_time=$(( $cur_tick_start_time + $INTERVAL ))
	sleep_time=0
	echo "Collecting monitoring data at: $(date +%s) (s)" | tee -a $log
	for server in $CLUSTER
	do
		#server_name=`echo "${SOURCE_MONITORING_DATA_DIR_CLUSTER}${server}" | awk -F "/" '{print $NF}'`
		server_source_dir="${SOURCE_MONITORING_DATA_DIR_CLUSTER}${server}/"
		server_target_dir="${TARGET_MONITORING_DATA_DIR}${server}/"
		mkdir -p $server_target_dir
		#SERVER="$server/"
		#echo "processing directory ${SERVER}" | tee -a $log
		rrds=""
		for metric in $METRICS
		do
			if [ -f $server_source_dir/$metric ]; then
				rrds="${rrds} ${server_source_dir}/${metric}"
			fi
		done
		
		for file in $rrds
		do
			#echo "Fetching file $f"
			fetched=`echo "$file" | awk -F "/" '{print $NF}' | awk -F ".rrd" '{print $1}'`
			#echo "fetched=${fetched}"
			#echo "After processing: $fetched"
			rrdtool fetch "$file" AVERAGE -e $(date +%s) -s e-3min >> $server_target_dir$fetched
		done
	done
	tick_counter=$(( $tick_counter + 1 ))
	cur_tick_end_time=$(date +%s)
	echo "Finished collecting monitoring data at: ${cur_tick_end_time} (s)" | tee -a $log
	sleep_time=$(( $next_tick_start_time-$cur_tick_end_time ))
	echo "Sleep for ${sleep_time} (s) until start of next fetch tick" | tee -a $log
	if [ $sleep_time -gt 0 ] ; then
		sleep $sleep_time
	fi
done
echo "Finished collecting of monitoring data for experiment ${NAME}."
exit
