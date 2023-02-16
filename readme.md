# Service Discover Tool with Solace PubSub+

## Purpose of use

Check the RTT of the applications connected to the topic of Solace to find the optimal destination server.

```
java -jar ServiceDiscovery-1.0-SNAPSHOT-jar-with-dependencies.jar -timeout 10 -interval 2
```



## Solace cloud

https://console.solace.cloud/

`Cluster Manager > All Services > Manage > Queues > Topic Endpoints`



## Topic Endpoints

### myapp/ping

### myapp/pong