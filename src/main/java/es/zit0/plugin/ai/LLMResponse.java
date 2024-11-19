package es.zit0.plugin.ai;
import es.zit0.plugin.traits.NPCAI.NPCAction;


public class LLMResponse {
    String action;
    String target;
    String message;
    String direction;
    double distance;

    public static LLMResponse parse(String rawResponse) {
        LLMResponse response = new LLMResponse();
        
        try {
            NPCAction actionType = NPCAction.fromResponse(rawResponse);
            response.action = actionType.name();
            
            // Remove the action prefix and trim
            String payload = rawResponse.substring(actionType.getPrefix().length()).replaceAll("[<>]", "").trim();;
            
            switch (actionType) {
                case HABLAR:
                    response.message = payload;
                    break;
                case SEGUIR:
                    response.target = payload;
                    break;
                case SALUDAR:
                    response.target = payload.trim();
                    break;
                case CAMINAR:
                    String[] parts = payload.split("\\s+");
                    if (parts.length >= 2) {
                        response.direction = parts[0].toLowerCase();
                        try {
                            response.distance = Double.parseDouble(parts[1]);
                        } catch (NumberFormatException e) {
                            // Default to a small distance if parsing fails
                            response.distance = 1;
                        }
                    }
                    break;
                default: // Default to talking
                    response.message = "Me he equivocado al usar la accion: " + actionType.name() + ". Lo intentaré de nuevo";
                    return response;
            }
        } catch (Exception e) {
            // More detailed fallback with logging
            System.err.println("Error parsing LLM response: " + rawResponse);
            response.action = NPCAction.HABLAR.name();
            response.message = "Lo siento, algo salió mal al procesar mi respuesta. Lo intentaré de nuevo.";
        }
        return response;
    }

    public String getTarget() {
        return target;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}