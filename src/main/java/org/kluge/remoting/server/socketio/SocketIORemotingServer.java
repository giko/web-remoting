package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.kluge.remoting.server.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingServer implements RemotingServer<String> {
    protected Map<RemotingSupervisor<String>, RemotingClient> activeConnections = new HashMap<>();
    protected Map<SocketIOClient, RemotingSupervisor<String>> supervisors = new HashMap<>();
    protected Map<SocketIOClient, UpdatableRemotingClient<String>> clients = new HashMap<>();

    protected SocketIOServer socketIOServer;

    public SocketIORemotingServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;

        socketIOServer.addConnectListener(client -> client.sendEvent("clients", getClientInfos()));

        socketIOServer.addEventListener("supervisor", LoginRequest.class, (client1, data2, ackSender1) -> {
            supervisors.put(client1, new SocketIORemotingSupervisor(client1));
        });
        socketIOServer.addEventListener("client", LoginRequest.class, (client1, data2, ackSender1) -> {
            clients.put(client1, new SocketIORemotingClient(client1));
        });
        socketIOServer.addEventListener("userinfo", UserInfo.class, (client, data, ackSender) -> {
            if (!clients.get(client).getInfo().equals(Optional.of(data))) {
                clients.get(client).setInfo(data);
                for (SocketIOClient supervisor : supervisors.keySet()) {
                    supervisor.sendEvent("clients", getClientInfos());
                }
            }
        });

        socketIOServer.addDisconnectListener(client -> {
            if (supervisors.get(client) != null) {
                RemotingSupervisor supervisor = supervisors.get(client);
                supervisor.unSupervise();
                supervisors.remove(client);

                return;
            }
            if (clients.get(client) != null) {
                RemotingClient<?> remotingClient = clients.get(client);
                remotingClient.getSession().getSupervisors().forEach(RemotingSupervisor::disconnectFromClient);
                clients.remove(client);
                for (SocketIOClient supervisor : supervisors.keySet()) {
                    supervisor.sendEvent("clients", getClientInfos());
                }
            }
        });

        socketIOServer.addEventListener("supervise", String.class, (client, data, ackSender) -> {
            RemotingSupervisor<String> supervisor = supervisors.get(client);
            RemotingClient<String> remotingClient = clients.get(socketIOServer.getClient(UUID.fromString(data)));

            supervisor.connect(remotingClient);

            activeConnections.put(supervisor, remotingClient);
            supervisor.supervise();
        });
        socketIOServer.addEventListener("unsupervise", Void.class,
                (client, data, ackSender) -> supervisors.get(client).disconnectFromClient());

        socketIOServer.addEventListener("showmessage", TextMessage.class,
                createRemoteOperationListener(RemotingClient::sendMessage));

        socketIOServer.addEventListener("requestreload", Void.class,
                createRemoteOperationListener((client, data1) -> client.refresh()));

        socketIOServer.addEventListener("screendata", String.class,
                (client, data, ackSender) -> clients.get(client).getSession().broadcast(data));
    }

    private List<ClientInfo> getClientInfos() {
        return clients.values().stream().map(ClientInfo::new).collect(Collectors.toList());
    }

    <T> RemoteOperationDataListener<T> createRemoteOperationListener(RemoteOperation<T> operation) {
        return new RemoteOperationDataListener<>(this, operation);
    }

    @Override public Set<RemotingClient> getRemotingClients() {
        return new LinkedHashSet<>(clients.values());
    }

    @Override public Set<RemotingSupervisor<String>> getSupervisors() {
        return new LinkedHashSet<>(supervisors.values());
    }

    @Override public Set<SharingSession<String>> getSessions() {
        throw new UnsupportedOperationException();
    }

    @Override public void addToSession(RemotingSupervisor<String> supervisor, RemotingClient client) {
        client.getSession().addSupervisor(supervisor);
    }

    @Override public void addRemotingClient(RemotingClient client) {

    }
}
