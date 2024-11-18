package es.zit0.plugin.traits.NPCAI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import es.zit0.plugin.Main;
import es.zit0.plugin.ai.LLMResponse;
import es.zit0.plugin.ai.LLMQueryService;
import es.zit0.plugin.chat.ChatMessage;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@TraitName("llmai")
public class NPCAI extends Trait {
    private final Main plugin;
    private final int CONTEXT_HISTORY_SIZE = 20;
    private NPCContext context;
    private boolean isActive = false;
    private static final Map<String, List<ChatMessage>> globalChatHistory = new HashMap<>();
    private final LLMQueryService llmQueryService;
    private boolean isQueryInProgress = false;


    public NPCAI() {
        super("llmai");
        this.plugin = Main.getInstance();
        this.llmQueryService = new LLMQueryService(plugin.getLlmApiUrl());
        this.context = new NPCContext(globalChatHistory);
    }

    @Override
    public void onSpawn() {
        plugin.getLogger().info("NPC " + npc.getName() + " con trait LLMAI ha aparecido.");
        isActive = true;
        this.context = new NPCContext(globalChatHistory); 
        context.setCurrentActivity("Recién spawneado");
        startAILoop();
        //makeInitialQuery();
    }



    private void startAILoop() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (npc != null && npc.isSpawned() && isActive && !isQueryInProgress) {
                isQueryInProgress = true;
                updateNPCAI(() -> {
                    isQueryInProgress = false;
                });
            }
        }, 0L, 300L); // 300L = 15 segundos
    }

    private void makeInitialQuery() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int maxRetries = 1;
            for (int retry = 0; retry < maxRetries; retry++) {
                try {
                    String systemPrompt = """
                    
                    IMPORTANTE: DEBES RESPONDER ÚNICAMENTE CON UNA DE ESTAS 3 ACCIONES:
                    SEGUIR <jugador>
                    CAMINAR <dirección> <número_entero>
                    SALUDAR <jugador>
                    HABLAR <mensaje>
                    Direcciones válidas: north, south, east, west, northeast, northwest, southeast, southwest
                    Si no hay jugadores o no sabes qué hacer, responde: CAMINAR <direccion> <numero> o tambien puedes hablar con los jugadores cercanos
                    NO PUEDE EXISTIR TEXTO ADICIONAL ANTES NI DESPUES DE LA ACCION; NO EXPLIQUES TU RAZONAMIENTO SOLO RESPONDE CON LA ACCIOn
                    NO ESCRIBAS NADA MÁS. SOLO LA ACCIÓN.
                    NO DES EXPLICACIONES.
                    NO USES OTROS FORMATOS.
                    RESPONDE SOLO CON LA ACCIÓN."""
                    .formatted(npc.getEntity().getType().toString(), npc.getName());

                    //String userContext = "Esto son datos del entorno de minecraft. No debes hablarle a los usuarios ni responder a los mensajes de entorno , solo toma decisiones y ejecuta acciones, ENTORNO: " + context.getContextString();
                    String userContext = "Elige una accion para el npc , recuerda responder solo con la accion en el formato correcto";

                    String response = LLMQueryService.builder()
                        .systemPrompt(systemPrompt)
                        .userContext(userContext)
                        .build(llmQueryService);

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        handleLLMResponse(response);
                    });
                } catch (Exception e) {
                    plugin.getLogger().warning("Error en consulta inicial LLM (Intento " + (retry + 1) + "): " + e.getMessage());
                
                    if (retry == maxRetries - 1) {
                        // Last retry failed
                        plugin.getLogger().severe("Todos los intentos de consulta inicial fallaron.");
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            handleLLMResponse("HABLAR algo ha salido mal"); // Fallback action
                        });
                    } else {
                        try {
                            Thread.sleep(5000 * (retry + 1)); // Exponential backoff
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }   
        });
    }
    private void updateNPCAI(Runnable onComplete) {
        updateContext();
        CompletableFuture.supplyAsync(() -> {
            int maxRetries = 1;
            for (int retry = 0; retry < maxRetries; retry++) {
                try {
                    String systemPrompt = """
Eres un %s en Minecraft llamado %s y tienes que tomar decisiones.
IMPORTANTE: DEBES RESPONDER ÚNICAMENTE CON UNA DE ESTAS ACCIONES:
SEGUIR <jugador>
CAMINAR <dirección> <número_entero>
SALUDAR <jugador>
HABLAR <mensaje>
Direcciones válidas: north, south, east, west, northeast, northwest, southeast, southwest
Si no hay jugadores o no sabes qué hacer, responde: CAMINAR north <numero> 
NO PUEDE EXISTIR TEXTO ADICIONAL ANTES NI DESPUES DE LA ACCION; NO EXPLIQUES TU RAZONAMIENTO SOLO RESPONDE CON LA ACCIOn
NO ESCRIBAS NADA MÁS. SOLO LA ACCIÓN.
NO DES EXPLICACIONES. 
NO USES OTROS FORMATOS.
RESPONDE SOLO CON LA ACCIÓN.""".formatted(npc.getEntity().getType().toString(), npc.getName());
                                           

                    String userContext = """
esto es informacion del entorno : 
%s
Puedes mantener conversaciones gracias al historial de mensajes recientes de jugadores cercanos.
que accion tomas? responde solo con la accion";""".formatted(context.getContextString());
               
        
                    return LLMQueryService.builder()
                        .systemPrompt(systemPrompt)
                        .userContext(userContext)
                        .build(llmQueryService);
                } catch (Exception e) {
                    plugin.getLogger().warning("Reintento " + (retry + 1) + " fallido: " + e.getMessage());
                    if (retry == maxRetries - 1) {
                        return "CAMINAR north 5"; // Fallback action
                    }
                    try {
                        Thread.sleep(5000 * (retry + 1)); // Exponential backoff
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            return "CAMINAR north 5"; // Fallback action
        }).thenAccept(response -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                handleLLMResponse(response);
                onComplete.run(); // Marca la consulta como completada
            });
        });
    }
    

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        
        // Ignorar si proviene del mismo npc
        if (playerName.equals(npc.getName())) {
            return;
        }
        
        String message = event.getMessage();
        long currentTime = System.currentTimeMillis();
        
        // Añadir mensaje al historial global con timestamp
        globalChatHistory.computeIfAbsent(playerName, k -> new ArrayList<>())
            .add(new ChatMessage(playerName, message, currentTime));

        // Eliminar mensajes más antiguos de 1 minuto y mantener el límite de tamaño
        List<ChatMessage> playerMessages = globalChatHistory.get(playerName);
        List<ChatMessage> updatedMessages = playerMessages.stream()
            .filter(chatMessage -> (currentTime - chatMessage.getTimestamp()) <= 60000) // 60000 ms = 1 minuto
            .collect(Collectors.toList());
        
        // Mantener solo los últimos CONTEXT_HISTORY_SIZE mensajes
        if (updatedMessages.size() > CONTEXT_HISTORY_SIZE) {
            updatedMessages = updatedMessages.subList(
                updatedMessages.size() - CONTEXT_HISTORY_SIZE,
                updatedMessages.size()
            );
        }
        
        globalChatHistory.put(playerName, updatedMessages);
    }

    private void updateContext() {
        if (!npc.isSpawned()) return;

        context.setLastLocation(npc.getEntity().getLocation());
        context.clearNearbyPlayers();

        // Actualizar jugadores cercanos
        npc.getEntity().getNearbyEntities(10, 10, 10).stream()
            .filter(entity -> entity instanceof Player)
            .forEach(entity -> context.addNearbyPlayer(((Player) entity).getName()));

        // Actualizar mensajes recientes de jugadores cercanos
        context.updateRecentMessages(10); // 10 bloques de radio

        context.setLastActionTime(System.currentTimeMillis());
    }

    @SuppressWarnings("unused")
    private void handleLLMResponse(String response) {
            LLMResponse aiResponse = LLMResponse.parse(response);
             // Parsear la acción desde la respuesta del modelo
            NPCAction action = NPCAction.fromResponse(response);
            // Obtener los argumentos que acompañan a la acción

            String[] parts = response.split(" ", 2); // Divide en acción y el resto
            String details = parts.length > 1 ? parts[1] : "";

            switch (action) {
                case HABLAR:
                    npc.getEntity().getWorld().getPlayers().forEach(player -> {
                        if (player.isOnline()) {
                            player.sendMessage("<" + npc.getName() + "> " + details);
                        }
                    });
                    context.addToHistory("NPC dijo: " + details);
                    break;

                case SEGUIR:
                    Player followTarget = Bukkit.getPlayer(aiResponse.getTarget());
                    if (followTarget != null) {
                        npc.getNavigator().setTarget(followTarget, true);
                        npc.getNavigator().getDefaultParameters().speedModifier(3.5f);
                        context.addToHistory("NPC está siguiendo a " + aiResponse.getTarget());
                    }
                    break;

                case SALUDAR:
                    Player greetTarget = Bukkit.getPlayer(aiResponse.getTarget());
                    if (greetTarget != null) {
                        String greeting = "¡Hola " + aiResponse.getTarget() + "!";       
                        context.addToHistory("NPC saludó a " + aiResponse.getTarget());
                        greetTarget.sendMessage("<" + npc.getName() + "> " + greeting);
                    }
                    break;
                case CAMINAR:
                    Location currentLoc = npc.getEntity().getLocation();
                    Location targetLoc = calculateTargetLocation(currentLoc, aiResponse.getDirection(), aiResponse.getDistance());
                    
                    if (targetLoc != null) {
                        npc.getNavigator().setTarget(targetLoc);
                        npc.getNavigator().getDefaultParameters().speedModifier(1.0f);
                        context.addToHistory("NPC está caminando hacia " + aiResponse.getDirection() 
                                            + " por " + aiResponse.getDistance() + " bloques");
                    }
                    break;
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