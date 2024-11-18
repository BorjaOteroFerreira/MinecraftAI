package es.zit0.plugin.traits;

import org.bukkit.Location;
import es.zit0.plugin.chat.ChatMessage;
import java.util.*;

public class NPCContext {
    private static final int CONTEXT_HISTORY_SIZE = 20;
    
    private List<String> conversationHistory = new ArrayList<>();
    @SuppressWarnings("unused")
    private Location lastLocation;
    @SuppressWarnings("unused")
    private long lastActionTime;
    private List<String> nearbyPlayers = new ArrayList<>();
    private String currentActivity;
    private List<ChatMessage> recentMessages = new ArrayList<>();
    private final Map<String, List<ChatMessage>> globalChatHistory;


    public NPCContext(Map<String, List<ChatMessage>> globalChatHistory) {
        this.globalChatHistory = globalChatHistory;
        this.currentActivity = "Iniciando";
        this.lastActionTime = System.currentTimeMillis();
    }
    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void addNearbyPlayer(String playerName) {
        nearbyPlayers.add(playerName);
    }

    public void clearNearbyPlayers() {
        nearbyPlayers.clear();
    }
    public String getCurrentActivity() {
        return currentActivity;
    }
    public String setCurrentActivity(String currentActivity) {
        return currentActivity;
    }

    public void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public void addToHistory(String event) {
        if (conversationHistory.size() >= CONTEXT_HISTORY_SIZE) {
            conversationHistory.remove(0);
        }
        conversationHistory.add(event);
    }

    public void updateRecentMessages(int radius) {
        recentMessages.clear();
        Set<String> nearbyPlayerNames = new HashSet<>(nearbyPlayers);
        
        // Obtener mensajes recientes de jugadores cercanos
        globalChatHistory.forEach((playerName, messages) -> {
            if (nearbyPlayerNames.contains(playerName)) {
                // Añadir los últimos 5 mensajes de cada jugador cercano
                int startIndex = Math.max(0, messages.size() - 5);
                recentMessages.addAll(messages.subList(startIndex, messages.size()));
            }
        });

        // Ordenar mensajes por timestamp
        recentMessages.sort(Comparator.comparingLong(ChatMessage::getTimestamp));

        // Mantener solo los últimos CONTEXT_HISTORY_SIZE mensajes
        if (recentMessages.size() > CONTEXT_HISTORY_SIZE) {
            recentMessages = recentMessages.subList(
                recentMessages.size() - CONTEXT_HISTORY_SIZE, 
                recentMessages.size()
            );
        }
    }

    public String getContextString() {
        StringBuilder context = new StringBuilder();
        context.append("Situación actual:\n");
        context.append("- Actividad actual: ").append(currentActivity).append("\n");
        context.append("- Jugadores cercanos: ").append(String.join(", ", nearbyPlayers)).append("\n");
        
        context.append("\nMensajes recientes:\n");
        recentMessages.forEach(msg -> 
            context.append("- ").append(msg.toString()).append("\n")
        );
        
        context.append("\nHistorial de acciones:\n");
        conversationHistory.forEach(event -> 
            context.append("- ").append(event).append("\n")
        );
        return context.toString();
    }
}