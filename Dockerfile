FROM openjdk:11

# TODO make assembling jar file during docker image build
COPY target/scala-2.13/home-monitor-assembly-0.1.jar /opt/home-monitor/server.jar

WORKDIR /opt/home-monitor

CMD java -jar server.jar by.jylilov.homemonitor.HomeMonitorHttpServer
