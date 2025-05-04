package com.example.demo.Controller;


import com.example.demo.Service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payment")
public class WebhookController {

    private final WebhookService webhookService;


    @PostMapping("/webhook")
    public ResponseEntity handleWebhook(
            @RequestHeader HttpHeaders headers,
            @RequestBody String rawBody) {
        webhookService.processWebhook(headers, rawBody);
        return ResponseEntity.ok().build();
    }
}
