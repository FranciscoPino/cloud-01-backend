package cl.duoc.cloudbackend.controller;

import java.util.Map;
import cl.duoc.cloudbackend.service.CognitoTokenService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    private final ObjectProvider<CognitoTokenService> cognitoTokenService;

    public DemoController(ObjectProvider<CognitoTokenService> cognitoTokenService) {
        this.cognitoTokenService = cognitoTokenService;
    }

    @GetMapping("/demo")
    public ResponseEntity<Map<String, String>> getDemo() {
        CognitoTokenService tokenService = cognitoTokenService.getIfAvailable();
        if (tokenService != null) {
            try {
                tokenService.obtainServiceToken();
            } catch (RestClientException exception) {
                logger.error("Cognito token exchange failed: {}", exception.getMessage());
                return ResponseEntity.status(503).body(Map.of(
                        "status", "unavailable",
                        "error", "No fue posible obtener credenciales de Cognito"
                ));
            }
        }
        return ResponseEntity.ok(Map.of(
            "message", "API Spring Boot operativa",
                "status", "ok"
        ));
    }
}