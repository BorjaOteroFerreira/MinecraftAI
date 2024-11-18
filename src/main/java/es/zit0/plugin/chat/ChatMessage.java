package es.zit0.plugin.chat;
public class ChatMessage {
    String sender;
    String message;
    long timestamp;

    public ChatMessage(String sender, String message, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getPlayerName() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return sender + ": " + message;
    }
}