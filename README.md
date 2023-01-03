![Snyk Vulnerabilities for GitHub Repo](https://img.shields.io/snyk/vulnerabilities/github/giko/web-remoting?label=backend%20vulnerabilities)
![Snyk Vulnerabilities for npm package version](https://img.shields.io/snyk/vulnerabilities/npm/web-remoting-client?label=client%20lib%20npm%20vulnerabilities)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/giko/web-remoting)

# About

This project allows site admins to see in realtime what users are doing on their website by transfering users dom by websocket.

![How it looks like](doc/presentation.gif)

# Backend

Written in java using netty and socket.io

Using docker run following command to start the server:

```bash
docker run -ti -p 8082:8082 -p 8085:8085 ghcr.io/giko/web-remoting:latest
```

# Frontend lib usage

```html
<script type="text/javascript" src="https://unpkg.com/web-remoting-client"></script>
<script type="text/javascript">
    // your remoting backend host and port goes here
    window.webRemotingHost = "http://localhost:8082";
    window.webRemotingClient.init(window.webRemotingHost);
</script>
```