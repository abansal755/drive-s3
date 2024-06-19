# This dockerfile is for serving ui
FROM ubuntu:latest

# Install curl
RUN apt-get update
RUN apt-get upgrade -y
RUN apt install curl -y

# Install node
WORKDIR /root
RUN curl -sL https://deb.nodesource.com/setup_20.x -o /tmp/nodesource_setup.sh
RUN bash /tmp/nodesource_setup.sh
RUN apt install nodejs -y

# Build
COPY ./ui /root/ui
WORKDIR /root/ui
RUN npm i
RUN npm run build

# Setup node server for serving the build
COPY ./serve /root/serve
WORKDIR /root/serve
RUN npm i

ENTRYPOINT node index.js