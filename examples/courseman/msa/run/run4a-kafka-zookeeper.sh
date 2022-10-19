#
# run KAFKA service with Courseman's topics
#
kafka_dir="/data/programs/kafka"

# Start the ZooKeeper service
$kafka_dir/bin/zookeeper-server-start.sh $kafka_dir/config/zookeeper.properties

