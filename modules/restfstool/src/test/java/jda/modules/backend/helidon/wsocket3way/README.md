# App: Web Socket 3-way

- POM file: pom-wsocket3.xml
- WEB root: main/resources/WEB3

## Scenario

Involves **3 entities**: 
1. web client 1 (CLI1): initiates request (R)
2. web server (SRV): process request R from CLI1 (supposedly does some processing with it) and logs an update on a queue to be sent to CLI2
3. web client 2 (CLI2): sends a socket request to SRV (with its id) asking for an update. SRV responses with update request (U) matching the id.

## References: 
[helidon example](https://helidon.io/docs/v2/#/se/websocket/01_overview)


