#!/bin/bash
#Script must be run as root!
#install zip
apt-get install -y zip
#download jdk
target_dir='/opt/oracle-java'
version_long='jdk1.7.0_17'
filename='jdk-7u17-linux-x64.tar.gz'
location='https://BUCKET_NAME.s3-eu-west-1.amazonaws.com/tar/'
wget --no-check-certificate "$location$filename"
#install jdk
#source: http://wiki.ubuntuusers.de/Java/Installation/Oracle_Java?redirect=no#Java-7-JDK
mkdir -p $target_dir
tar -xzf $filename -C $target_dir
rm $filename
#setup jdk
update-alternatives --install "/usr/bin/java" "java" "/opt/oracle-java/$version_long/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/oracle-java/$version_long/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/oracle-java/$version_long/bin/javaws" 1
update-alternatives --install "/usr/bin/jar" "jar" "/opt/oracle-java/$version_long/bin/jar" 1
#browser-plugin not required
update-alternatives --set "java" "/opt/oracle-java/$version_long/bin/java"
update-alternatives --set "javac" "/opt/oracle-java/$version_long/bin/javac"
update-alternatives --set "javaws" "/opt/oracle-java/$version_long/bin/javaws"
update-alternatives --set "jar" "/opt/oracle-java/$version_long/bin/jar"

# Install Cryptography extension
file='UnlimitedJCEPolicyJDK7.zip'
wget --no-check-certificate "$location$file"
unzip $file
cp -f UnlimitedJCEPolicy/local_policy.jar $target_dir/$version_long/jre/lib/security/
cp -f UnlimitedJCEPolicy/US_export_policy.jar $target_dir/$version_long/jre/lib/security/
rm -rf UnlimitedJCEPolicy
