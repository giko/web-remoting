package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.kluge.remoting.server.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingServer<T> implements RemotingServer<T> {
    protected Map<SocketIOClient, RemotingSupervisor<T>> supervisors = new HashMap<>();
    protected Map<SocketIOClient, UpdatableRemotingClient<T>> clients = new HashMap<>();
    protected SharingSessionFactory<T> sessionFactory;

    protected SocketIOServer socketIOServer;

    public SocketIORemotingServer(SocketIOServer socketIOServer, Class<T> dataClass,
                                  SharingSessionFactory<T> sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.socketIOServer = socketIOServer;

        socketIOServer.addEventListener("supervisor", LoginRequest.class, (client1, data2, ackSender1) -> {
                    supervisors.put(client1, new SocketIORemotingSupervisor<>(client1));
                    clients.values().stream().map(ClientInfo::new).forEach(clientInfo -> client1.sendEvent("client_updated", clientInfo));
                }
        );

        socketIOServer.addEventListener("client", LoginRequest.class, (client1, data2, ackSender1) -> {
                    clients.put(client1, new SocketIORemotingClient<>(client1, sessionFactory));
                    notifyUpdated(clients.get(client1));
                }
        );

        socketIOServer.addEventListener("userinfo", UserInfo.class, (client, data, ackSender) -> {
            if (!clients.get(client).getInfo().equals(Optional.of(data))) {
                clients.get(client).setInfo(data);
                notifyUpdated(clients.get(client));
            }
            clients.get(client).setInfo(data);
        });

        socketIOServer.addDisconnectListener(client -> {
            if (supervisors.get(client) != null) {
                RemotingSupervisor<? extends T> supervisor = supervisors.get(client);
                supervisor.unSupervise();
                supervisors.remove(client);

                return;
            }
            if (clients.get(client) != null) {
                RemotingClient<?> remotingClient = clients.get(client);
                remotingClient.getSession().getSupervisors().forEach(RemotingSupervisor::disconnectFromClient);
                clients.remove(client);
                notifyDisconnected(remotingClient);
            }
        });

        socketIOServer.addEventListener("super", String.class, (client, data, ackSender) -> {
            RemotingSupervisor<T> supervisor = supervisors.get(client);
            RemotingClient<T> remotingClient = clients.get(socketIOServer.getClient(UUID.fromString(data)));

            supervisor.connect(remotingClient);

            supervisor.supervise();
        });

        socketIOServer.addEventListener("supervise", String.class, (client, data, ackSender) -> {
            RemotingSupervisor<T> supervisor = supervisors.get(client);
            RemotingClient<T> remotingClient = clients.get(socketIOServer.getClient(UUID.fromString(data)));

            supervisor.connect(remotingClient);

            supervisor.supervise();
        });
        socketIOServer.addEventListener("unsupervise", Void.class,
                (client, data, ackSender) -> supervisors.get(client).disconnectFromClient());

        socketIOServer.addEventListener("showmessage", TextMessage.class,
                createRemoteOperationListener(RemotingClient::sendMessage));

        socketIOServer.addEventListener("requestreload", Void.class,
                createRemoteOperationListener(RemotingClient::refresh));

        socketIOServer.addEventListener("countdown", Long.class, createRemoteOperationListener(
                RemotingClient::displayCountDown));

        socketIOServer.addEventListener("screendata", dataClass,
                (client, data, ackSender) -> clients.get(client).getSession().broadcast(data));
    }

    private void broadcastToSupervisors(String event, Object... objects) {
        for (SocketIOClient supervisor : supervisors.keySet()) {
            supervisor.sendEvent(event, objects);
        }
    }

    private void notifyUpdated(UpdatableRemotingClient<T> client) {
        broadcastToSupervisors("client_updated", new ClientInfo(client));
    }

    private void notifyDisconnected(RemotingClient<?> client) {
        broadcastToSupervisors("client_disconnected", client.getUUID().toString());
    }

    private List<ClientInfo> getClientInfos2() {
        return clients.values().stream().map(ClientInfo::new).collect(Collectors.toList());
    }

    <K> RemoteOperationDataListener<T, K> createRemoteOperationListener(RemoteOperation<T, K> operation) {
        return new RemoteOperationDataListener<>(this, operation);
    }

    @Override
    public Set<RemotingClient<T>> getRemotingClients() {
        return new LinkedHashSet<>(clients.values());
    }

    @Override
    public Set<RemotingSupervisor<T>> getSupervisors() {
        return new LinkedHashSet<>(supervisors.values());
    }

    @Override
    public Set<SharingSession<T>> getSessions() {
        throw new UnsupportedOperationException();
    }

    //Use case?
    @Override
    public void addRemotingClient(RemotingClient<T> client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSuprevisor(RemotingSupervisor<T> supervisor) {
        throw new UnsupportedOperationException();
    }
}
