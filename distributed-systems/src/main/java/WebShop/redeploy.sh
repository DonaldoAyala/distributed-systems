#!/bin/bash

rm -rf /home/R2019630415/apache-tomcat-8.5.77/webapps/Servicio.war
rm -rf /home/R2019630415/apache-tomcat-8.5.77/webapps/Servicio

rm /home/R2019630415/DirWebShop/Servicio.war
rm /home/R2019630415/DirWebShop/WebShop/*.class

export CATALINA_HOME=/home/R2019630415/apache-tomcat-8.5.77

javac -cp $CATALINA_HOME/lib/javax.ws.rs-api-2.0.1.jar:$CATALINA_HOME/lib/gson-2.3.1.jar:. WebShop/Servicio.java

rm WEB-INF/classes/WebShop/*
cp WebShop/*.class WEB-INF/classes/WebShop/
jar cvf Servicio.war WEB-INF META-INF

cp /home/R2019630415/DirWebShop/Servicio.war /home/R2019630415/apache-tomcat-8.5.77/webapps/
