#!/bin/bash
if [ $# -lt 1 ]; then
echo "Usage: $0 <experiment_folder>"
exit 1
fi

for folder in $1/*
do
#check to prevent double-normalization
if [ -d $folder/original ]; then
echo "Found \"original\"-folder in one of the subfolders, cancelling normalization."
exit 1
fi

if [ -d $folder ]; then
	mkdir -p "$folder/original"
	for file in $folder/*
	do
		if [ -f $file ]; then
		awk -F': ' '$2 !~ /-nan/ {print $1, $2}' $file \
		| awk '!/sum/ {print}' \
		| awk 'NF{print}' \
		| awk '!x[$0]++' \
		> "${file}_clean"
		mv $file $folder/original
		fi
	done
fi
done
