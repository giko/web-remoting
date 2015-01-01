package org.kluge;

import com.corundumstudio.socketio.SocketIOClient;
import org.kluge.server.RemotingClient;

import java.util.UUID;

/**
 * Created by giko on 12/29/14.
 */
public class ClientInfo {
    private String userName;
    private String url;
    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public ClientInfo(RemotingClient client) {
        uuid = client.getUUID();
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
