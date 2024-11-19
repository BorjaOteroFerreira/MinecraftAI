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
        this.context = new NPCContext(globalChatHistory, super.getName());
        
    }

    @Override
    public void onSpawn() {
        plugin.getLogger().info("NPC " + npc.getName() + " con trait LLMAI ha aparecido.");
        isActive = true;
        this.context = new NPCContext(globalChatHistory, super.getName()); 
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
HABLAR <mensaje>
CAMINAR <dirección> <número_entero>
Direcciones válidas: north, south, east, west, northeast, northwest, southeast, southwest,  el numero de bloques a desplazarte es un entero
Si no hay jugadores o no sabes qué hacer, caminar para buscar gente en la direccion que te apetezca. 
NO PUEDE EXISTIR TEXTO ADICIONAL ANTES NI DESPUES DE LA ACCION; NO EXPLIQUES TU RAZONAMIENTO SOLO RESPONDE CON LA ACCION
SI LA CONVERSACION ES REPETITIVA NO HABLES.
NO ESCRIBAS NADA MÁS. SOLO LA ACCIÓN.
NO DES EXPLICACIONES. 
NO USES OTROS FORMATOS.
RESPONDE SOLO CON LA ACCIÓN.""".formatted(npc.getEntity().getType().toString(), npc.getName());
                                           

                    String userContext = """
esto es informacion del entorno : 
%s
Puedes mantener conversaciones gracias al historial de mensajes recientes de jugadores cercanos.
que accion tomas? responde solo con la accion";""".formatted(context.getContextString());
               
                    Thread.sleep(6000); // Exponential backoff
                    return LLMQueryService.builder()
                        .systemPrompt(systemPrompt)
                        .userContext(userContext)
                        .build(llmQueryService);
                } catch (Exception e) {
                    plugin.getLogger().warning("Reintento " + (retry + 1) + " fallido: " + e.getMessage());
                    if (retry == maxRetries - 1) {
                        return "HABLAR algo salio mal"; // Fallback action
                    }
                    try {
                        Thread.sleep(6000 * (retry + 1)); // Exponential backoff
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            return "HABLAR algo salio mal"; // Fallback action
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
        String message = event.getMessage();
        long currentTime = System.currentTimeMillis();
    
        // Si el mensaje es de cualquier entidad (jugador u otro NPC), excepto este NPC específico
        if (!playerName.equals(this.npc.getName())) {
            List<ChatMessage> playerMessages = globalChatHistory.computeIfAbsent(
                playerName, 
                k -> new ArrayList<>()
            );
    
            // Verificar si el mensaje ya existe
            boolean isDuplicate = playerMessages.stream()
                .anyMatch(chatMessage -> 
                    chatMessage.getMessage().equals(message) && 
                    (currentTime - chatMessage.getTimestamp()) <= 30000 // Solo considera duplicados en los últimos 30 segundos
                );
    
            if (!isDuplicate) {
                // Añadir mensaje al historial global con timestamp
                playerMessages.add(new ChatMessage(playerName, message, currentTime));
    
                // Limpiar mensajes antiguos solo para este jugador/NPC
                List<ChatMessage> updatedMessages = playerMessages.stream()
                    .filter(chatMessage -> (currentTime - chatMessage.getTimestamp()) <= 30000)
                    .collect(Collectors.toList());
                
                // Mantener límite de tamaño
                if (updatedMessages.size() > CONTEXT_HISTORY_SIZE) {
                    updatedMessages = updatedMessages.subList(
                        updatedMessages.size() - CONTEXT_HISTORY_SIZE,
                        updatedMessages.size()
                    );
                }
                
                globalChatHistory.put(playerName, updatedMessages);
            }
        }
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
                    context.addToHistory(npc.getName()+ " dijo: " + details);
                    break;

                case SEGUIR:
                    Player followTarget = Bukkit.getPlayer(aiResponse.getTarget());
                    if (followTarget != null) {
                        npc.getNavigator().setTarget(followTarget, true);
                        npc.getNavigator().getDefaultParameters().speedModifier(3.5f);
                        context.addToHistory(npc.getName()+ " está siguiendo a " + aiResponse.getTarget());
                    }
                    break;

                case SALUDAR:
                    Player greetTarget = Bukkit.getPlayer(aiResponse.getTarget());
                    if (greetTarget != null) {
                        String greeting = "¡Hola " + aiResponse.getTarget() + "!";       
                        context.addToHistory(npc.getName()+" saludó a " + aiResponse.getTarget());
                        greetTarget.sendMessage("<" + npc.getName() + "> " + greeting);
                    }
                    break;
                case CAMINAR:
                    Location currentLoc = npc.getEntity().getLocation();
                    Location targetLoc = calculateTargetLocation(currentLoc, aiResponse.getDirection(), aiResponse.getDistance());
                    
                    if (targetLoc != null) {
                        npc.getNavigator().setTarget(targetLoc);
                        npc.getNavigator().getDefaultParameters().speedModifier(3.0f);
                        context.addToHistory(npc.getName() + " está caminando hacia " + aiResponse.getDirection() 
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