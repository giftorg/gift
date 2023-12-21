FROM node:alpine3.18 AS builder

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build

FROM nginx:latest

ENV API_HOST=http://localhost:8000

COPY --from=builder --chown=nginx:nginx /app/dist /usr/share/nginx/html
COPY config/nginx.tmp /etc/nginx/conf.d/

RUN rm /etc/nginx/conf.d/default.conf

WORKDIR /usr/share/nginx/html

ENTRYPOINT envsubst '$API_HOST' < /etc/nginx/conf.d/nginx.tmp > /etc/nginx/conf.d/nginx.conf && nginx -g 'daemon off;'
