sudo docker build -t voriq-frontend .
sudo docker run -p 8000:80 -d voriq-frontend
