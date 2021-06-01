**How to run in loop**
tmux new
while true; do java -jar EnergySalesBackend.jar; sleep 1; done



**How to prepare execution env:**
sudo useradd backend_user
sudo passwd backend_user
mkdir EnergySalesBackend
cd EnergySalesBackend
sudo chown -R backend_user .


**How to copy:**
scp out/artifacts/EnergySalesBackend_jar/* trevol@192.168.87.8:/home/trevol/EnergySalesBackend
scp db/* trevol@192.168.87.8:/home/trevol/EnergySalesBackend/db
