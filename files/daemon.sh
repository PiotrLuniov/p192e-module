ssh root@34.67.225.132

cat << EOF > /etc/docker/daemon.json 
 {
 "insecure-registries" : ["http://nexus-ci.playpit.by:6566"]
 }
EOF
systemctl restart docker


listen 443 ssl default_server;
location ^~ /.well-known/acme-challenge/ {
	allow all;
}

 server_name your_domain.com;
        ssl_certificate /etc/nginx/ssl/nginx.crt;
        ssl_certificate_key /etc/nginx/ssl/nginx.key;



 server {
        listen 80 default_server;
        listen [::]:80 default_server ipv6only=on;

        listen 443 ssl;

        root /usr/share/nginx/html;
        index index.html index.htm;

        server_name your_domain.com;
        ssl_certificate /etc/nginx/ssl/nginx.crt;
        ssl_certificate_key /etc/nginx/ssl/nginx.key;

        location / {
                try_files $uri $uri/ =404;
        }
}