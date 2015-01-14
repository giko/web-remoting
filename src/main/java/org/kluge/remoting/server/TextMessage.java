package org.kluge.remoting.server;

/**
 * Created by giko on 12/25/14.
 */
public class TextMessage {
    private String message;
    private String title;
    private String type;

    public TextMessage() {
    }

    public TextMessage(String message, String title, String type) {
        this.message = message;
        this.title = title;
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
}
