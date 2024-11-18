package es.zit0.plugin.chat;
public class ChatMessage {
    String sender;
    String message;
    long timestamp;

    public ChatMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return sender + ": " + message;
    }
}