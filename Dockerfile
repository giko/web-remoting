FROM ventra/centos:latest

ADD remoting.jar /remoting.jar
ADD supervisord-remoting.conf /etc/supervisor/conf.d/supervisord-remoting.conf
