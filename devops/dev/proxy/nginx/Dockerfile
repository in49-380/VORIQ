FROM nginx:latest

# Устанавливаем Certbot и пакеты, необходимые для Nginx-плагина Certbot
RUN apt-get update && \
    apt-get install -y certbot python3-certbot-nginx && \
    rm -rf /var/lib/apt/lists/*

# Команда запуска Nginx
CMD ["nginx", "-g", "daemon off;"]