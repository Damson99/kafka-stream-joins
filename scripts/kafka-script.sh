#USER LOGIN
#netsh interface portproxy add v4tov4 listenport=9092 listenaddress=0.0.0.0 connectport=9092 connectaddress=<your-ip>
#
#confluent local services start
#
#kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic payment-request
#
#kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic payment-confirmation
#
#kafka-console-producer --broker-list localhost:9092 --topic payment-request \
#--property parse.key=true --property key.separator=":"
#
#kafka-console-producer --broker-list localhost:9092 --topic payment-confirmation \
#--property parse.key=true --property key.separator=":"
#
#confluent local destroy

#=====ADVERT CLICKS=====
netsh interface portproxy add v4tov4 listenport=9092 listenaddress=0.0.0.0 connectport=9092 connectaddress=<your-ip>

confluent local services start

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic active-inventory

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic ad-click

kafka-console-producer --broker-list localhost:9092 --topic active-inventory \
--property parse.key=true --property key.separator=":"

kafka-console-producer --broker-list localhost:9092 --topic ad-click \
--property parse.key=true --property key.separator=":"

confluent local destroy