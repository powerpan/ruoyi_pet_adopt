FROM node:20-bullseye AS build

WORKDIR /build
COPY pet-app/package*.json ./
RUN npm ci --legacy-peer-deps

COPY pet-app/ ./
RUN npm run build:prod

FROM nginx:1.25-alpine

COPY deploy/nginx/pet.conf /etc/nginx/conf.d/default.conf
COPY --from=build /build/dist /usr/share/nginx/html

EXPOSE 80
