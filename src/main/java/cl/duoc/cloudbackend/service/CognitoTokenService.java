package cl.duoc.cloudbackend.service;

import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Profile("azure")
public class CognitoTokenService {

    private final RestClient restClient;
    private final String tokenUri;
    private final String clientId;
    private final String clientSecret;
    private final String scopes;

    public CognitoTokenService(
            RestClient.Builder restClientBuilder,
            @Value("${COGNITO_TOKEN_URI}") String tokenUri,
            @Value("${COGNITO_CLIENT_ID}") String clientId,
            @Value("${COGNITO_CLIENT_SECRET}") String clientSecret,
            @Value("${COGNITO_SCOPES:}") String scopes) {
        this.restClient = restClientBuilder.build();
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scopes = scopes;
    }

    public boolean obtainServiceToken() {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        if (!scopes.isBlank()) {
            form.add("scope", scopes);
        }

        Map<?, ?> response = restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .body(form)
                .retrieve()
                .body(Map.class);

        return response != null && response.get("access_token") != null;
    }
}
