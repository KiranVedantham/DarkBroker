############################################################
# Dockerfile to build spring boot app container images
# Based on Ubuntu
############################################################

FROM ubuntu:latest

MAINTAINER Kiran Vedantham

RUN apt-get update

RUN apt-get install default-jre -y

RUN apt-get install default-jdk -y

RUN apt-get install maven -y

ADD pom.xml /app/

ADD src/ /app/src/

ADD WebContent/ /app/WebContent/
ADD src/main/resources/Sample.csv /app/WebContent/WEB-INF/classes/
ADD src/main/resources/upfiles/ /app/WebContent/WEB-INF/classes/test/



WORKDIR /app/

RUN mvn package

EXPOSE  8080


CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","target/DarkBorker-1.war"]