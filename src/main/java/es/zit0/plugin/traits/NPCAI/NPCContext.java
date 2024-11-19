package es.zit0.plugin.traits.NPCAI;

import org.bukkit.Location;
import es.zit0.plugin.chat.ChatMessage;

import java.time.LocalDateTime;
import java.util.*;

public class NPCContext {
    private static final int CONTEXT_HISTORY_SIZE = 60;
    
    private List<String> conversationHistory = new ArrayList<>();
    @SuppressWarnings("unused")
    private Location lastLocation;
    @SuppressWarnings("unused")
    private long lastActionTime;
    private List<String> nearbyPlayers = new ArrayList<>();
    private String currentActivity;
    private List<ChatMessage> recentMessages = new ArrayList<>();
    private final Map<String, List<ChatMessage>> globalChatHistory;
    private LocalDateTime timeNow = LocalDateTime.now();
    String npcName;

    public NPCContext(Map<String, List<ChatMessage>> globalChatHistory, String npcName) {
        this.globalChatHistory = globalChatHistory;
        this.currentActivity = "Iniciando";
        this.lastActionTime = System.currentTimeMillis();
        this.npcName = npcName;
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


    public LocalDateTime getTimeNow() {
        return timeNow;
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
        context.append("Datos de fecha y hora: " ).append(timeNow).append("\n");
        context.append("Situación actual:\n");
        context.append("- Actividad actual: ").append(currentActivity).append("\n");
        context.append("- Jugadores cercanos: ").append(String.join(", ", nearbyPlayers)).append("\n");
        context.append("\nMensajes recientes:\n");
           // Filtrar mensajes, excluyendo los del NPC actual
           for (Map.Entry<String, List<ChatMessage>> entry : globalChatHistory.entrySet()) {
            if (!entry.getKey().equals(npcName)) {  // npcName sería el nombre de este NPC
                for (ChatMessage message : entry.getValue()) {
                    context.append(message.getPlayerName())
                          .append(": ")
                          .append(message.getMessage())
                          .append("\n");
                }
            }
        }
        context.append("\nHistorial de acciones:\n");
        conversationHistory.forEach(event -> 
            context.append("- ").append(event).append("\n")
        );
        return context.toString();
    }
}