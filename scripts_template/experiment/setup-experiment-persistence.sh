#!/bin/bash
device='/dev/xvdh'
mounting='/mnt/experiment-data'
sudo apt-get install -y s3cmd rrdtool
sudo mkdir $mounting
sudo mount $device $mounting
sudo chown -R ubuntu $mounting
