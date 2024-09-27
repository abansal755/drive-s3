FROM ubuntu:latest

# Install jdk17
RUN apt-get update
RUN apt-get upgrade -y
RUN apt install openjdk-17-jdk openjdk-17-jre -y

# Install wget
RUN apt install wget -y

# Install maven
WORKDIR /root
RUN wget https://dlcdn.apache.org/maven/maven-3/3.9.7/binaries/apache-maven-3.9.7-bin.tar.gz
RUN tar xzvf apache-maven-3.9.7-bin.tar.gz
ENV PATH="${PATH}:/root/apache-maven-3.9.7/bin"

# Copy project files
COPY . /root/auth-service
WORKDIR /root/auth-service

# Build
RUN mvn package -DskipTests

ENTRYPOINT ["java", "-jar", "target/auth-0.0.1-SNAPSHOT.jar"]