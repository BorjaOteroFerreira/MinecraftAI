package es.zit0.plugin.traits;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import es.zit0.plugin.Main;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@TraitName("llmai")
public class LLMAITrait extends Trait {
    private final Main plugin;
    private final Gson gson = new Gson();
    private final int CONTEXT_HISTORY_SIZE = 20;
    private NPCContext context;
    private boolean isActive = false;
    private static final Map<String, List<ChatMessage>> globalChatHistory = new HashMap<>();


    private class ChatMessage {
        String sender;
        String message;
        long timestamp;

        public ChatMessage(String sender, String message) {
            this.sender = sender;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return sender + ": " + message;
        }
    }

    private class NPCContext {
        List<String> conversationHistory = new ArrayList<>();
        Location lastLocation;
        long lastActionTime;
        List<String> nearbyPlayers = new ArrayList<>();
        String currentActivity;
        List<ChatMessage> recentMessages = new ArrayList<>();

        public NPCContext() {
            this.currentActivity = "Iniciando";
            this.lastActionTime = System.currentTimeMillis();
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
            recentMessages.sort(Comparator.comparingLong(m -> m.timestamp));

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

    public LLMAITrait() {
        super("llmai");
        this.plugin = Main.getInstance();
        this.context = new NPCContext();
    }


    @Override
    public void onSpawn() {
        plugin.getLogger().info("NPC " + npc.getName() + " con trait LLMAI ha aparecido.");
        isActive = true;
        this.context = new NPCContext();
        context.currentActivity = "Recién spawneado";
        startAILoop();
        makeInitialQuery();
    }

    private String queryLLM(String contextInfo) throws IOException, URISyntaxException {
        URL url = new URI(plugin.getLlmApiUrl()).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        String systemPrompt = """
            Eres un " %s" amigable en Minecraft llamado %s. Tu objetivo es interactuar con los jugadores
            de manera natural y amistosa. DEBES responder SOLO con UNA de estas acciones exactas:
            SEGUIR <jugador> 
            CAMINAR <dirección> <distancia> formato esperado : CAMINAR north/south/east/west/northeast/northwest/southeast/southwest distancia
            HABLAR <mensaje que quieres decir>
            SALUDAR <jugador>
            No te quedes mucho tiempo en el mismo sitio. usa CAMINAR
            No dejes de seguir al jugador mientras le hablas.
            Puedes seguir al jugador para no perderle de vista.
            Te gusta mucho CAMINAR.
            Cuando no sepas qué hacer, simplemente responde con "HABLAR <mensaje>".
            """.formatted(npc.getEntity().getType().toString(),npc.getName());

        String userPrompt = """

            Situación actual:
            %s
            
            ¿Qué acción tomarías? Responde solo con una de las acciones permitidas.
            """.formatted(contextInfo);

        String jsonInputString = gson.toJson(Map.of(
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
            ),
            "temperature", 0.7,
            "max_tokens", 150
          
        ));

        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonInputString.getBytes());
            os.flush();
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
        return jsonResponse.getAsJsonArray("choices")
                          .get(0).getAsJsonObject()
                          .getAsJsonObject("message")
                          .get("content").getAsString();
    }

    private void startAILoop() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (npc != null && npc.isSpawned() && isActive) {
                updateNPCAI();
            }
        }, 20L, 300L); 
    }

    private void makeInitialQuery() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String response = queryLLM("Acabas de aparecer en el mundo como un NPC. " + 
                                        context.getContextString());

                Bukkit.getScheduler().runTask(plugin, () -> {
                    handleLLMResponse(response);
                });
            } catch (Exception e) {
                plugin.getLogger().severe("Error en consulta inicial LLM: " + e.getMessage());
                e.printStackTrace();
                Bukkit.getScheduler().runTaskLater(plugin, this::makeInitialQuery, 100L);
            }
        });
    }

    private void updateNPCAI() {
        updateContext();

        CompletableFuture.supplyAsync(() -> {
            try {
                return queryLLM(context.getContextString());
            } catch (Exception e) {
                plugin.getLogger().severe("Error en consulta LLM: " + e.getMessage());
                e.printStackTrace();
                return "HABLAR Lo siento, estoy teniendo problemas técnicos.";
            }
        }).thenAccept(response -> {
            Bukkit.getScheduler().runTask(plugin, () -> handleLLMResponse(response));
        });
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();
        
        // Añadir mensaje al historial global
        globalChatHistory.computeIfAbsent(playerName, k -> new ArrayList<>())
            .add(new ChatMessage(playerName, message));

        // Mantener solo los últimos CONTEXT_HISTORY_SIZE mensajes por jugador
        List<ChatMessage> playerMessages = globalChatHistory.get(playerName);
        if (playerMessages.size() > CONTEXT_HISTORY_SIZE) {
            playerMessages = playerMessages.subList(
                playerMessages.size() - CONTEXT_HISTORY_SIZE,
                playerMessages.size()
            );
            globalChatHistory.put(playerName, playerMessages);
        }
    }

    private void updateContext() {
        if (!npc.isSpawned()) return;

        context.lastLocation = npc.getEntity().getLocation();
        context.nearbyPlayers.clear();

        // Actualizar jugadores cercanos
        npc.getEntity().getNearbyEntities(10, 10, 10).stream()
            .filter(entity -> entity instanceof Player)
            .forEach(entity -> context.nearbyPlayers.add(((Player) entity).getName()));

        // Actualizar mensajes recientes de jugadores cercanos
        context.updateRecentMessages(10); // 10 bloques de radio

        context.lastActionTime = System.currentTimeMillis();
    }

    private void handleLLMResponse(String response) {
        if (response.startsWith("HABLAR ")) {
            String message = response.substring(7).trim();
            globalChatHistory.computeIfAbsent(npc.getName(), k -> new ArrayList<>())
                .add(new ChatMessage(npc.getName(), message));
            
            npc.getEntity().getWorld().getPlayers().forEach(player -> {
                if (player.isOnline()) {
                    player.sendMessage("<" + npc.getName() + "> " + message);
                }
            });
            context.addToHistory("NPC dijo: " + message);
        } else if (response.startsWith("SEGUIR ")) {
            String playerName = response.substring(7).trim();
            Player target = Bukkit.getPlayer(playerName.replace("<", "").replace(">", ""));
            if (target != null) {
                npc.getNavigator().setTarget(target, true);
                npc.getNavigator().getDefaultParameters().speedModifier(3.5f);
                context.addToHistory("NPC está siguiendo a " + playerName);
            }
        } else if (response.startsWith("SALUDAR ")) {
            String playerName = response.substring(8).trim();
            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                String greeting = "¡Hola " + playerName + "!";
                globalChatHistory.computeIfAbsent(npc.getName(), k -> new ArrayList<>())
                    .add(new ChatMessage(npc.getName(), greeting));
                
                context.addToHistory("NPC saludó a " + playerName);
                target.sendMessage("<" + npc.getName() + "> " + greeting);
            }
        } else if (response.startsWith("CAMINAR ")) {
            // Formato esperado: "CAMINAR north/south/east/west/northeast/northwest/southeast/southwest distancia"
            String[] parts = response.substring(8).trim().split(" ");
            if (parts.length >= 2) {
                String direction = parts[0].toLowerCase();
                try {
                    double distance = Double.parseDouble(parts[1]);
                    Location currentLoc = npc.getEntity().getLocation();
                    Location targetLoc = calculateTargetLocation(currentLoc, direction, distance);
                    
                    if (targetLoc != null) {
                        npc.getNavigator().setTarget(targetLoc);
                        npc.getNavigator().getDefaultParameters().speedModifier(1.0f);
                        context.addToHistory("NPC está caminando hacia " + direction + " por " + distance + " bloques");
                    }
                } catch (NumberFormatException e) {
                    context.addToHistory("Error: Distancia inválida especificada");
                }
            }
        }
    }

    private Location calculateTargetLocation(Location currentLoc, String direction, double distance) {
        Location targetLoc = currentLoc.clone();
        
        switch (direction) {
            case "north":
                targetLoc.add(0, 0, -distance);
                break;
            case "south":
                targetLoc.add(0, 0, distance);
                break;
            case "east":
                targetLoc.add(distance, 0, 0);
                break;
            case "west":
                targetLoc.add(-distance, 0, 0);
                break;
            case "northeast":
                targetLoc.add(distance * 0.707, 0, -distance * 0.707); // 0.707 es aproximadamente √2/2
                break;
            case "northwest":
                targetLoc.add(-distance * 0.707, 0, -distance * 0.707);
                break;
            case "southeast":
                targetLoc.add(distance * 0.707, 0, distance * 0.707);
                break;
            case "southwest":
                targetLoc.add(-distance * 0.707, 0, distance * 0.707);
                break;
            default: 
                return null;
        }
        return targetLoc;
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        if (event.getNPC() == this.npc) {
            context.addToHistory("El jugador " + event.getClicker().getName() + " interactuó con el NPC.");
            npc.getEntity().getWorld().getPlayers().forEach(player -> player.sendMessage("¡Hola, soy " + npc.getName() + "!"));
        }
    }
    @Override
    public void onDespawn() {
        isActive = false;
    }
}
