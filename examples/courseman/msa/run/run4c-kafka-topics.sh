#
# run KAFKA service with Courseman's topics
#
if [ -z $1 ]
then
  kafka_dir="/data/programs/kafka"
else
  kafka_dir=$1
fi

# Create topic: "streams-courseman-coursemodules"
$kafka_dir/bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic courseChangeTopic --bootstrap-server localhost:9092
$kafka_dir/bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic studentChangeTopic --bootstrap-server localhost:9092
$kafka_dir/bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic addressChangeTopic --bootstrap-server localhost:9092
$kafka_dir/bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic classChangeTopic --bootstrap-server localhost:9092
$kafka_dir/bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic enrolmentChangeTopic --bootstrap-server localhost:9092
$kafka_dir/bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic teacherChangeTopic --bootstrap-server localhost:9092
