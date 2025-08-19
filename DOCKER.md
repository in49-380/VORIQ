sudo docker build -t voriq-frontend .
sudo docker run -p 8000:80 -d voriq-frontend

sudo docker ps        # only runned
sudo docker ps -a     # all

docker stop <container_id>
docker rm <container_id>
<!-- !!!!!! -->
sudo docker compose -f ../infrastucture/docker-compose.yml up --build -d
sudo docker compose logs -f frontend
sudo docker compose -f ../infrastucture/docker-compose.yml up -d
sudo docker compose -f ../infrastucture/docker-compose.yml down

sudo exec -it <id> sh
docker compose exec frontend-dev env | grep VITE_GOOGLE_CLIENT_ID
