echo "Starting infrastructure services..."
        
run_dir=/data/projects/jda/examples/courseman/msa/run

echo "==> Starting config server..."
gnome-terminal\
  --tab --title="config-server" --command="bash -c 'cd $run_dir; ./run1*; $SHELL'"

sleep 40
echo "==> Starting discovery server..."

gnome-terminal\
  --tab --title="discovery-server" --command="bash -c 'cd $run_dir; ./run2*; $SHELL'"

sleep 25

echo "==> Starting gateway server..."
gnome-terminal\
  --tab --title="gw-server" --command="bash -c 'cd $run_dir; ./run3*; $SHELL'"
