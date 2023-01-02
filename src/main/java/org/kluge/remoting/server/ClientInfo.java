package org.kluge.remoting.server;

import java.util.UUID;

/**
 * Created by giko on 12/29/14.
 */
public class ClientInfo {
    private UUID uuid;
    private UserInfo info;

    public ClientInfo(RemotingClient<?> client) {
        uuid = client.getUUID();
        info = client.getInfo();
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

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
