FROM tomcat:7-jre7-alpine

RUN mkdir -p /srv/trailmagic.com/www /srv/trailmagic.com/logs
COPY www /srv/trailmagic.com/www
COPY docker-build/lib/ /srv/trailmagic.com/lib/
COPY docker-build/bin/ /usr/local/tomcat/bin/
COPY conf /srv/trailmagic.com/conf
COPY photo/target/photo.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080 8443

RUN rm -rf /usr/local/tomcat/webapps/ROOT
