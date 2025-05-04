package com.example.demo.Service;

import com.example.demo.API.ApiException;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class CheckoutService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Value("${lemon.squeezy.store.id}")
    private String storeId;

    public CheckoutService(WebClient webClient, UserRepository userRepository, ProductRepository productRepository) {
        this.webClient = webClient;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Mono<String> checkout(String productId) {
        return webClient.post()
                .uri("/checkout/{productId}", productId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public String createCheckout(String variantId, Integer userId) {

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        Product product = productRepository.findProductById(Integer.valueOf(variantId));
        if (product == null) {
            throw new ApiException("Product not found");
        }
        if (user.getProducts().contains(product)) {
            throw new ApiException("User already owns this product");
        }
        if (storeId == null || storeId.isEmpty()) {
            throw new ApiException("Store ID is missing");
        }
        String payload = String.format("""
            {
                "checkout": {
                    "attributes": {
                        "store_id": "%s",
                        "variant_id": "%s",
                        "custom_data": {
                            "user_id": %d
                        }
                    }
                }
            }
        """, storeId, variantId, userId);
        String response = webClient.post()
                .uri("/checkouts")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractCheckoutUrl(response);
    }
    public String extractCheckoutUrl(String response) {
        JSONObject json = new JSONObject(response);
        return json.getJSONObject("data")
                .getJSONObject("attributes")
                .getString("url");
    }

}
