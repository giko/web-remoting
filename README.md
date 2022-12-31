# About

This project allows site admins to see in realtime what users are doing on their website by transfering users dom by websocket.

# Backend

Written in java using netty and socket.io

Using docker run following command to start the server:

```bash
docker run -ti -p 8082:8082 -p 8085:8085 ghcr.io/giko/web-remoting:latest
```