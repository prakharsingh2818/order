# order


sudo docker build -t order --target build --no-cache .

sudo docker run -p 7000:7000 order


docker rmi $(docker images --filter "dangling=true" -q --no-trunc)


To run from inside the image:
1. Fetch the images using `sudo docker images`
2. Get inside the image `sudo docker run -it <image_id> /bin/bash`
3. unzip target/universal/*.zip 
4. cd order-0.1.0-SNAPSHOT/
5. bin/order -Dplay.http.secret.key=your-secret-key -Dhttp.port=7000
6. bin/order -Dplay.http.secret.key=your-secret-key -Dhttp.port=7000 -Dhttp.address=0.0.0.0
7. docker build -t order-app-image .
8. docker run -p 7000:7000 -d --name order-app order-app-image