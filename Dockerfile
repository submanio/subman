FROM ubuntu:14.04
MAINTAINER Vladimir Iakovlev <nvbn.rm@gmail.com>

RUN adduser --disabled-password --gecos "" subman
RUN adduser subman sudo

ENV "VERSION" 2015_01_12_02_16

RUN apt-get update -yqq
RUN apt-get upgrade -yqq
RUN apt-get install software-properties-common python-software-properties -yqq --no-install-recommends
RUN add-apt-repository ppa:chris-lea/node.js  -y
RUN apt-get update -yqq
RUN apt-get install nodejs ruby openjdk-7-jdk curl -yqq --no-install-recommends
RUN npm install -g bower
RUN gem install sass
RUN curl -s https://raw.githubusercontent.com/technomancy/leiningen/2.5.0/bin/lein > /usr/local/bin/lein
RUN chmod 0755 /usr/local/bin/lein

WORKDIR /home/subman
COPY . /home/subman/code
RUN chown -R subman code
USER subman
WORKDIR /home/subman/code

RUN lein deps
RUN lein cljx once
RUN lein bower install
RUN lein with-profile production cljsbuild once >> /dev/null 2>> /dev/null

WORKDIR resources/public/
RUN sass main.sass > main.css
WORKDIR ../..

VOLUME /var/static
