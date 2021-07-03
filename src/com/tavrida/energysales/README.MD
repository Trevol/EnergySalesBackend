**How to run in loop**
tmux new
while true; do ./EnergySalesBackend; sleep 1; done



**How to prepare execution env:**
sudo useradd backend_user
sudo passwd backend_user
mkdir EnergySalesBackend
cd EnergySalesBackend
sudo chown -R backend_user .


**How to copy:**
scp build/distributions/EnergySalesBackend-0.0.1.tar trevol@192.168.87.8:/home/trevol/

tar -xvf EnergySalesBackend-0.0.1.tar
cd EnergySalesBackend-0.0.1/bin
mkdir databases

scp databases/ENERGY_SALES.mv.db trevol@192.168.87.8:/home/trevol/EnergySalesBackend-0.0.1/bin/databases

scp trevol@192.168.87.8:/home/trevol/EnergySalesBackend-0.0.1/bin/databases/ENERGY_SALES.mv.db .
