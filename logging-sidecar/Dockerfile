# adapted from https://github.com/kubernetes/contrib/tree/master/logging/fluentd-sidecar-gcp
FROM ubuntu:16.04

# Disable prompts from apt.
ENV DEBIAN_FRONTEND noninteractive
# Keeps unneeded configs from being installed along with fluentd.
ENV DO_NOT_INSTALL_CATCH_ALL_CONFIG true

# Install the Fluentd agent that knows how to send logs to Google Cloud Logging.
RUN apt-get -q update && \
    apt-get install -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* && \
    curl -s https://storage.googleapis.com/signals-agents/logging/google-fluentd-install.sh | bash

COPY google-fluentd.conf /etc/google-fluentd/google-fluentd.conf
COPY tomcat-access.log /etc/google-fluentd/files/tomcat-access.log

CMD /usr/sbin/google-fluentd -qq --use-v1-config --suppress-repeated-stacktrace > /var/log/google-fluentd/google-fluentd.log