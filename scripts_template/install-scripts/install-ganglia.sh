#!/bin/bash
export DEBIAN_FRONTEND=noninteractive
apt-get install -y ganglia-monitor
#overwrite /etc/ganglia/gmond.conf for cluster
while [ ! -f config.fin ]; do
echo "waiting for configuration to complete.."
sleep 1
done
cp -f configs/ganglia/gmond.conf /etc/ganglia/gmond.conf
#restart ganglia daemon
/etc/init.d/ganglia-monitor restart

