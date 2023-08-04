#
# run KAFKA service with Courseman's topics
#
if [ -z $1 ]
then
  kafka_dir="/data/programs/kafka"
else
  kafka_dir=$1
fi

# Start the Kafka broker service
$kafka_dir/bin/kafka-server-start.sh $kafka_dir/config/server.properties

