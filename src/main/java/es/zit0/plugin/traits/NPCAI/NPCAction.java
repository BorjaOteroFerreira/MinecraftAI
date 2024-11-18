package es.zit0.plugin.traits.NPCAI;

public enum NPCAction {
    TALK("HABLAR"),
    FOLLOW("SEGUIR"),
    GREET("SALUDAR"),
    WALK("CAMINAR");

    private final String prefix;

    NPCAction(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static NPCAction fromResponse(String response) {
        for (NPCAction action : values()) {
            if (response.startsWith(action.getPrefix() + " ")) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown action: " + response);
    }
}