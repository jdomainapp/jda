#
# run KAFKA service with Courseman's topics
#
kafka_dir="/data/programs/kafka"

# Start the Kafka broker service
$kafka_dir/bin/kafka-server-start.sh $kafka_dir/config/server.properties

