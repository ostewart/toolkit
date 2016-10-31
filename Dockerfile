FROM tomcat:7-jre7-alpine

COPY docker-build/lib/ /srv/trailmagic.com/lib/
COPY docker-build/setenv.sh /usr/local/tomcat/bin/
COPY conf /srv/trailmagic.com/conf
COPY photo/target/photo.war /usr/local/tomcat/webapps/ROOT.war
RUN rm -rf /usr/local/tomcat/webapps/ROOT
