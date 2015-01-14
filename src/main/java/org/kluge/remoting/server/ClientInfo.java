package org.kluge.remoting.server;

import org.kluge.remoting.server.RemotingClient;
import org.kluge.remoting.server.UserInfo;

import java.util.UUID;

/**
 * Created by giko on 12/29/14.
 */
public class ClientInfo {
    private UUID uuid;
    private UserInfo info;

    public ClientInfo(RemotingClient<?> client) {
        uuid = client.getUUID();
        info = client.getInfo().get();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    public UUID getUuid() {
        return uuid;
    }
}
