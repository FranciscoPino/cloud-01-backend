package cl.duoc.cloudbackend.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/demo")
    public ResponseEntity<Map<String, String>> getDemo() {
        return ResponseEntity.ok(Map.of(
            "message", "API Spring Boot operativa",
                "status", "ok"
        ));
    }
}