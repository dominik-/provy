#!/bin/bash
#run as root!
export DEBIAN_FRONTEND=noninteractive
apt-get install -y ganglia-monitor gmetad ganglia-webfrontend
while [ ! -f config.fin ]; do
echo "waiting for configuration to complete.."
sleep 1
done
#update /etc/gmetad.conf and /etc/gmetad.conf for receiver
cp -f configs/ganglia/* /etc/ganglia/
#create directories for round-robin database and www-interface
mkdir -p /var/lib/ganglia/rrds/
chown -R ganglia:ganglia /var/lib/ganglia/
mkdir /var/www/ganglia
chown -R ganglia:ganglia /var/www/ganglia/
cp -r /usr/share/ganglia-webfrontend /var/www/ganglia
#restart daemons
/etc/init.d/ganglia-monitor restart
/etc/init.d/gmetad restart
#restart apache
/etc/init.d/apache2 restart
