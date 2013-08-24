#!/bin/bash
#runs script given as parameter, downloaded from default location
scripts='https://BUCKET_NAME.s3-eu-west-1.amazonaws.com/scripts/install-scripts'
if [ $# -ne 1 ]
	then 
		echo "Usage: runit scriptfile"
	else
		wget $scripts/$1.sh
		chmod +x $1.sh
		./$1.sh
fi
