FROM tomcat:7-jre7-alpine

RUN mkdir -p /srv/trailmagic.com/www /srv/trailmagic.com/logs
COPY www /srv/trailmagic.com/www
COPY docker-build/lib/ /srv/trailmagic.com/lib/
COPY docker-build/bin/ /usr/local/tomcat/bin/
COPY docker-build/conf/ /usr/local/tomcat/conf/
COPY conf/jdbc.properties /srv/trailmagic.com/conf/jdbc.properties
COPY photo/target/photo.war /srv/trailmagic.com/webapps/ROOT.war

EXPOSE 8080 8443
