package es.zit0.plugin.traits.NPCAI;

public enum NPCAction {
    HABLAR("HABLAR"),
    SEGUIR("SEGUIR"),
    SALUDAR("SALUDAR"),
    CAMINAR("CAMINAR");

    private final String prefix;

    NPCAction(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static NPCAction fromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return HABLAR; 
        }

        for (NPCAction action : values()) {
            if (response.toUpperCase().trim().startsWith(action.prefix)) {
                return action;
            }
        }

        // If no matching action found, default to talking
        return HABLAR;
    }
}