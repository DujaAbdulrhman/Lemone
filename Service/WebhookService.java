package com.example.demo.Service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Model.PurchaseHistory;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.PurchaseHistoryRepository;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
@RequiredArgsConstructor
@Service
public class WebhookService {

    @Value("${lemon.squeezy.webhook.secret}")
    private String secret;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;



    public void processWebhook(HttpHeaders headers, String rawBody) {
        try {
            String eventType = headers.getFirst("X-Event-Name");
            String signature = headers.getFirst("X-Signature");

            if (!verifySignature(rawBody, signature)) {
                throw new SecurityException("Invalid signature.");
            }

            if ("order_created".equals(eventType)) {
                handleOrderCreated(rawBody);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to process webhook", e);
        }
    }

    private boolean verifySignature(String rawBody, String signature)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] hashBytes = sha256_HMAC.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
        String computedSignature = Hex.encodeHexString(hashBytes);

        return computedSignature.equals(signature);
    }

    private void handleOrderCreated(String rawBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(rawBody);

            int userId = root.path("meta").path("custom_data").path("user_id").asInt();
            String variantId = root.path("meta").path("custom_data").path("variant_id").asText();
            String orderNumber = root.path("data").path("attributes").path("order_number").asText();
            String status = root.path("data").path("attributes").path("status").asText();
            String totalFormatted = root.path("data").path("attributes").path("total_formatted").asText();
            String productName = root.path("data").path("attributes").path("first_order_item").path("product_name").asText();

            if (!"paid".equalsIgnoreCase(status)) {
                throw new IllegalStateException("Order is not paid.");
            }

            Product product = productRepository.findProductByVariantId(variantId);
            if (product == null) {
                throw new IllegalStateException("Book not found for variantId: " + variantId);
            }

            User user = userRepository.findUserById(userId);
            if (user == null) {
                throw new IllegalStateException("User not found with id: " + userId);
            }

            // ربط العلاقة
            product.getUsers().add(user);
            user.getProducts().add(product);
            productRepository.save(product);
            userRepository.save(user);

            // إنشاء سجل الشراء
            PurchaseHistory history = new PurchaseHistory();
            history.setUser(user);
            history.setProduct(product);
            history.setOrderNumber(orderNumber);
            history.setProductName(productName);
            history.setTotalFormatted(totalFormatted);
            purchaseHistoryRepository.save(history);

        } catch (Exception e) {
            throw new RuntimeException("Error processing order_created webhook", e);
        }
    }
}
