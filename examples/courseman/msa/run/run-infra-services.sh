echo "Starting infrastructure services..."
        
run_dir=/data/projects/jda/examples/courseman/msa/run

echo "==> Starting config server..."
gnome-terminal\
  --tab --title="config-server" --command="bash -c 'cd $run_dir; ./run1*; $SHELL'"

sleep 30
echo "==> Starting discovery server..."

gnome-terminal\
  --tab --title="discovery-server" --command="bash -c 'cd $run_dir; ./run2*; $SHELL'"

sleep 15

echo "==> Starting gateway server..."
gnome-terminal\
  --tab --title="gw-server" --command="bash -c 'cd $run_dir; ./run3*; $SHELL'"

echo "==> Starting KAFKA services..."

echo "==> Starting Kafka/zookeeper server..."
gnome-terminal\
  --tab --title="kaf-zoo" --command="bash -c 'cd $run_dir; ./run4a*; $SHELL'"

sleep 15

echo "==> Starting Kafka server..."
gnome-terminal\
  --tab --title="kaf-server" --command="bash -c 'cd $run_dir; ./run4b*; $SHELL'"

#gnome-terminal\
# --tab --title="config-server" --command="bash -c 'cd $run_dir; ./run1*; $SHELL'"\
# --tab --title="discovery-server" --command="bash -c 'cd $run_dir; ./run2*; $SHELL'"\
# --tab --title="gw-server" --command="bash -c 'cd $run_dir; ./run3*; $SHELL'"
        
