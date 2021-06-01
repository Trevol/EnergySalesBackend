**How to run:**
java -jar EnergySalesBackend.jar

**How to run in loop**

while true; do java -jar EnergySalesBackend.jar; done





**How to prepare execution env:**
sudo useradd backend_user
sudo passwd backend_user
mkdir EnergySalesBackend
cd EnergySalesBackend
sudo chown -R backend_user .


**How to copy:**
scp out/artifacts/EnergySalesBackend_jar/EnergySalesBackend.jar backend_user@192.168.0.106:/home/trevol/EnergySalesBackend
scp db backend_user@192.168.0.106:/home/trevol/EnergySalesBackend
