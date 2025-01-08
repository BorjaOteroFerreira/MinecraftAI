package es.zit0.plugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import es.zit0.plugin.traits.NPCAI.NPCAI;

public class Main extends JavaPlugin implements Listener {
    private static Main instance;
    private String llmApiUrl;
    private boolean traitsRegistered = false;

    @Override
    public void onEnable() {
        instance = this;
        if (!CitizensAPI.hasImplementation()) {
            getLogger().warning("Citizens no está cargado. Asegúrate de que esté en la carpeta de plugins.");
            return;
        }
        // Ahora puedes registrar el trait
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCAI.class).withName("llmai"));

        // Verificar que Citizens esté presente
        if (!getServer().getPluginManager().isPluginEnabled("Citizens")) {
            getLogger().severe("Citizens 2.0 no encontrado o no habilitado!");
            getLogger().severe("Este plugin requiere Citizens 2.0 para funcionar!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Cargar configuración
        saveDefaultConfig();
        loadConfiguration();
        // Registrar eventos
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("NPC AI LLM Plugin Activado! Esperando a que el servidor termine de cargar...");
        registerTrait();
    }
    

    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        config.addDefault("llm-api-url", "http://127.0.0.1:1234/v1/chat/completions");
        config.options().copyDefaults(true);
        saveConfig();

        llmApiUrl = config.getString("llm-api-url");
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        // Esperar hasta que el servidor esté completamente cargado
        getServer().getScheduler().runTaskLater(this, () -> {
            if (!traitsRegistered) {
                registerTrait();
            }
        }, 100L); // 5 segundos después de que el servidor termine de cargar
    }

    private void registerTrait() {
        Boolean exist = !CitizensAPI.getTraitFactory().getRegisteredTraits().stream().noneMatch(trait -> trait.getTraitClass().equals(NPCAI.class));
        try {
            if (CitizensAPI.getTraitFactory() != null  ) {
                if (exist) {
                    getLogger().info("Trait LLMAI ya está registrado!");
                    return;
                }
                CitizensAPI.getTraitFactory().registerTrait(
                    TraitInfo.create(NPCAI.class).withName("llmai")
                );
                traitsRegistered = true;
                getLogger().info("¡Trait LLMAI registrado exitosamente!");
            } else {
                getLogger().warning("Citizens API aún no está lista. Reintentando en 5 segundos...");
                getServer().getScheduler().runTaskLater(this, this::registerTrait, 100L);
            }
        } catch (Exception e) {
            getLogger().severe("Error al registrar el trait LLMAI: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("NPC AI LLM Plugin Desactivado!");
    }

    public static Main getInstance() {
        return instance;
    }

    public String getLlmApiUrl() {
        return llmApiUrl;
    }
}
