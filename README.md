provy
=====

Java framework for cluster management and elasticity-related experiments.

This project comes 'as is', i.e. without any warranty or support. Use with care and cost awareness!

------------------------------
Prerequisites
------------------------------

- Amazon Web Services account with...
   - S3 Access
   - EC2 Access
   - EC2 Security-group called 'hbase'
   - EC2 Key-Pair (save *.pem file on local PC) for the latter security group
   - An EBS volume for experimental results

SECURITY WARNING: do not use AWS key-pairs or S3 buckets, which are in productive usage or provide access to any private data. They will be stored on publicly accessible locations / used as public locations! Also, a disclosed location is not a secure location!

- The following software packages must be located on the S3 bucket in the folder 'tar':
   - jdk-7u17-linux-x64.tar.gz
   - UnlimitedJCEPolicyJDK7.zip
   - hadoop-1.0.4.tar.gz
   - hbase-0.94.6.tar.gz
   - zookeeper-3.4.5.tar.gz
   - ycsb-0.1.4.tar.gz

NOTE: YCSB has to be modified to be usable with these specific HBase and Hadoop versions. A fork of the original YCSB fulfilling these requirements can be found at https://github.com/dominik-/YCSB. The remaining tarballs can be acquired in public archives.

-------------------------------
Setup
-------------------------------

- Build the maven project
- Copy your private key to a location accessible by Java
- Either define AWS_ACCESS_KEY_ID and AWS_SECRET_KEY as environment variables or enter the keys in the project under 'aws/credentials.properties'
- Create/modify experiment setup configurations. The default configuration is the 'experiments/scaleout-experiment.properties'. To run it, add the bucket name, path to the private key file and the EBS volume ID.

--------------------------------
Running
--------------------------------

- Project main file is at src/test/DefaultScalingExperiment.java
- Change the experiment name for every run!
- After launching, wait 3 to 5 minutes for the cluster to come up
- In the Java console, the following commands must be run in order to finish an experiment:
	1. prepare
	2. load
	3. maxtp-read OR maxtp-update
	4. start-read X Y Z OR start-update X Y Z
	with: X target throughput, Y number of YCSB threads, Z added nodes (scale-out dimension)
- YCSB and Ganglia measurements are stored on the EBS volume
- Launch information and command logs in the Java project under 'logs'

--------------------------------
Modifying the setup and configuration
--------------------------------

- The main Java files that determine the initial cluster composition and node sizes are:
	-src/experiment/elasticity/ScalingExperimentSetup
	-src/experiment/elasticity/ScaleoutExperiment
	-src/experiment/test/DefaultScalingExperiment
- Also consider modifying HBase and Hadoop settings in the config files in 'configs_template'
- Cluster bootup / setup is determined by shell-scripts in 'setups_template' and 'scripts_template'


