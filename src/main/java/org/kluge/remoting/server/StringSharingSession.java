package org.kluge.remoting.server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by giko on 1/1/15.
 */
public class StringSharingSession implements SharingSession<String> {
    RemotingClient<String> client;
    Set<RemotingSupervisor<String>> supervisors = new HashSet<>();

    public StringSharingSession(RemotingClient<String> client) {
        this.client = client;
    }

    @Override public void broadcast(String data) {
        Document document = Jsoup.parse(data);
        document.outputSettings().prettyPrint(false);
        client.getInfo().ifPresent(userInfo -> document.select("[href], [src]").forEach(element -> {
            if (!element.attr("href").startsWith("http")) {
                element.attr("href", userInfo.getLocation() + "/" + element.attr("href"));
            }
            if (!element.attr("src").startsWith("http")) {
                element.attr("src", userInfo.getLocation() + "/" + element.attr("src"));
            }
        }));
        document.select("iframe").remove();
        document.select("script").remove();
        supervisors.forEach(supervisor -> supervisor.send(document.html()));
    }

    @Override public RemotingClient getClient() {
        return client;
    }

    @Override public Set<RemotingSupervisor<String>> getSupervisors() {
        return new HashSet<>(supervisors);
    }

    @Override public void addSupervisor(RemotingSupervisor<String> supervisor) {
        supervisors.add(supervisor);
        updateBroadcastState();
    }

    @Override public void removeSupervisor(RemotingSupervisor<String> supervisor) {
        supervisors.remove(supervisor);
        updateBroadcastState();
    }

    private void updateBroadcastState() {
        if (supervisors.isEmpty()) {
            client.stopSharing();
        } else {
            client.startSharing();
        }
    }
}
