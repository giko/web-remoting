package org.kluge.remoting.server.socketio;

/**
 * Created by giko on 3/10/15.
 */
public class PongRequest {
    private Long time;
    private String html;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
