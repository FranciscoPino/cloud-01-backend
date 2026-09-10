package cl.duoc.cloudbackend.controller;

import java.util.Map;
import cl.duoc.cloudbackend.service.CognitoTokenService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final ObjectProvider<CognitoTokenService> cognitoTokenService;

    public DemoController(ObjectProvider<CognitoTokenService> cognitoTokenService) {
        this.cognitoTokenService = cognitoTokenService;
    }

    @GetMapping("/demo")
    public ResponseEntity<Map<String, String>> getDemo() {
        CognitoTokenService tokenService = cognitoTokenService.getIfAvailable();
        if (tokenService != null) {
            tokenService.obtainServiceToken();
        }
        return ResponseEntity.ok(Map.of(
            "message", "API Spring Boot operativa",
                "status", "ok"
        ));
    }
}