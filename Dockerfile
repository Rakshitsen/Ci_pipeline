FROM nginx:alpine                        # Lightweight, fast NGINX image
COPY . /usr/share/nginx/html/            # Copies website files to NGINX’s default root
EXPOSE 80                                # Exposes port 80
ENTRYPOINT ["nginx","-g","daemon off;"]  # Keeps NGINX running in foreground (container won’t exit)
