#
# run KAFKA service with Courseman's topics
#
if [ -z $1 ]
then
  kafka_dir="/data/programs/kafka"
else
  kafka_dir=$1
fi

# Start the ZooKeeper service
$kafka_dir/bin/zookeeper-server-start.sh $kafka_dir/config/zookeeper.properties

