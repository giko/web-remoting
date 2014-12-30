package org.kluge;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * Created by giko on 12/29/14.
 */
public class ClientInfo {
    private String userName;
    private String url;

    public ClientInfo(SocketIOClient client) {
        userName = client.get("id").toString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
