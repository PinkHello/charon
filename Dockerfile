FROM java
RUN curl -O https://mirror.bit.edu.cn/apache/kafka/2.6.0/kafka_2.12-2.6.0.tgz \
    && tar -xvf kafka_2.12-2.6.0.tgz && rm kafka_2.12-2.6.0.tgz
WORKDIR /kafka_2.12-2.6.0
RUN mkdir connect
ADD lib/ libs/
ADD config/ config/
ADD build/libs connect/
EXPOSE 9998
EXPOSE 9999
ENTRYPOINT exec sh bin/connect-standalone.sh \
        config/connect-standalone.properties \
        config/connect-charon-sink.properties \
        config/connect-charon-source.properties



