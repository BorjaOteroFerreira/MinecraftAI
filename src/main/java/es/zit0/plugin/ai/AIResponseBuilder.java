package es.zit0.plugin.ai;
import es.zit0.plugin.traits.NPCAI.NPCAction;


public class AIResponseBuilder {
    String action;
     String target;
    String message;
    String direction;
    double distance;

    public static AIResponseBuilder parse(String rawResponse) {
        AIResponseBuilder response = new AIResponseBuilder();
        try {
            NPCAction actionType = NPCAction.fromResponse(rawResponse);
            response.action = actionType.name();

            String payload = rawResponse.substring(actionType.getPrefix().length() + 1).trim();

            switch (actionType) {
                case TALK:
                    response.message = payload;
                    break;
                case FOLLOW:
                    response.target = payload.replace("<", "").replace(">", "");
                    break;
                case GREET:
                    response.target = payload;
                    break;
                case WALK:
                    String[] parts = payload.split(" ");
                    if (parts.length >= 2) {
                        response.direction = parts[0].toLowerCase();
                        response.distance = Double.parseDouble(parts[1]);
                    }
                    break;
            }
        } catch (Exception e) {
            // Fallback to generic talk action
            response.action = NPCAction.TALK.name();
            response.message = "Lo siento, algo salió mal al procesar mi respuesta.";
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