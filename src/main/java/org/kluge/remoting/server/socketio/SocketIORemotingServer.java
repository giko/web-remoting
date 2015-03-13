package org.kluge.remoting.server.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.kluge.remoting.server.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by giko on 12/30/14.
 */
public class SocketIORemotingServer<T> implements RemotingServer<T> {
    protected final Map<SocketIOClient, RemotingSupervisor<T>> supervisors = new ConcurrentHashMap<>();
    protected final Map<SocketIOClient, UpdatableRemotingClient<T>> clients = new ConcurrentHashMap<>();
    protected final SocketIOServer socketIOServer;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    protected SharingSessionFactory<T> sessionFactory;

    public SocketIORemotingServer(SocketIOServer socketIOServer, Class<T> dataClass,
                                  SharingSessionFactory<T> sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.socketIOServer = socketIOServer;

        socketIOServer.addEventListener("supervisor", LoginRequest.class, (client1, data2, ackSender1) -> {
                    supervisors.putIfAbsent(client1, new SocketIORemotingSupervisor<>(client1));
                    clients.values().stream().map(ClientInfo::new).forEach(clientInfo -> client1.sendEvent("client_updated", clientInfo));
                }
        );

        socketIOServer.addEventListener("client", LoginRequest.class, (client1, data2, ackSender1) -> {
                    clients.put(client1, new SocketIORemotingClient<>(client1, sessionFactory));
                    scheduler.scheduleAtFixedRate(new PingTask(clients.get(client1)), 10, 180, TimeUnit.SECONDS);
                }
        );

        socketIOServer.addEventListener("pongDom", PongRequest.class, (socketIOClient, pongRequest, ackRequest) -> {
            clients.get(socketIOClient).getInfo().ifPresent(info -> info.setPing(System.currentTimeMillis() - pongRequest.getTime()));
            notifyUpdated(clients.get(socketIOClient));
        });

        socketIOServer.addEventListener("userinfo", UserInfo.class, (client, data, ackSender) -> {
            data.setLocation(data.getLocation().substring(0, data.getLocation().length() > 30 ? 30 : data.getLocation().length()));
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

        socketIOServer.addEventListener("toggle_connect", String.class, (client, data, ackSender) -> {
            RemotingSupervisor<T> supervisor = supervisors.get(client);
            RemotingClient<T> remotingClient = clients.get(socketIOServer.getClient(UUID.fromString(data)));

            if (supervisor.getBatchableRemotingClients().contains(remotingClient)) {
                supervisor.disconnectFromClient(remotingClient);
                return;
            }

            supervisor.connect((BatchableRemotingClient) remotingClient);
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
                createRemoteOperationListener(BatchableRemotingClient::sendMessage));

        socketIOServer.addEventListener("requestreload", Void.class,
                createRemoteOperationListener(BatchableRemotingClient::refresh));
        
        socketIOServer.addEventListener("requestping", Void.class,
                createRemoteOperationListener(BatchableRemotingClient::ping));

        socketIOServer.addEventListener("countdown", Long.class, createRemoteOperationListener(
                BatchableRemotingClient::displayCountDown));

        socketIOServer.addEventListener("screendata", dataClass,
                (client, data, ackSender) -> clients.get(client).getSession().broadcast(data));
    }

    private void broadcastToAll(Set<SocketIOClient> clients, String event, Object... objects) {
        clients.forEach(supervisor -> supervisor.sendEvent(event, objects));
    }

    private void notifyUpdated(UpdatableRemotingClient<T> client) {
        broadcastToAll(supervisors.keySet(), "client_updated", new ClientInfo(client));
    }

    private void notifyDisconnected(RemotingClient<?> client) {
        broadcastToAll(supervisors.keySet(), "client_disconnected", client.getUUID().toString());
    }

    <K> RemoteOperationDataListener<T, K> createRemoteOperationListener(RemoteOperation<K> operation) {
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
