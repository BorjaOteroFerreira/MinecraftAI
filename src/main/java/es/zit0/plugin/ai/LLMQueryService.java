package es.zit0.plugin.ai;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class LLMQueryService {
    private final String apiUrl;
    private final Gson gson;

    public LLMQueryService(String apiUrl) {
        this.apiUrl = apiUrl;
        this.gson = new Gson();
    }

    public String queryLLM(String systemPrompt, String userContext) throws Exception {
        URL url = new URI(apiUrl).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.setConnectTimeout(30000);
        con.setReadTimeout(30000);

        String jsonInputString = gson.toJson(Map.of(
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userContext)
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

    public static class PromptBuilder {
        private String systemPrompt;
        private String userContext;

        public PromptBuilder systemPrompt(String prompt) {
            this.systemPrompt = prompt;
            return this;
        }

        public PromptBuilder userContext(String context) {
            this.userContext = context;
            return this;
        }

        public String build(LLMQueryService service) throws Exception {
            if (systemPrompt == null || userContext == null) {
                throw new IllegalStateException("System prompt and user context must be set");
            }
            return service.queryLLM(systemPrompt, userContext);
        }
    }

    public static PromptBuilder builder() {
        return new PromptBuilder();
    }
}