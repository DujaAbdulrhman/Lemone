package com.example.demo.Service;

import com.example.demo.API.ApiException;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LemoneSqueesyService {


    private final WebClient lemonSqueezyWebClient;  // تم تغيير الاسم لتجنب التعارض

   // private final WebClient webClient;
    private final CheckoutService checkoutService;
    private final ProductService productService;  // استخدام ProductService بدلاً من ProductController

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Value("${lemon.squeezy.store.id}")
    private String storeId;  // يجب أن يبقى هذا هنا

    // استخدم هذه الطريقة لقراءة المنتجات من Lemon Squeezy API
    public String getProducts() {
        String response = lemonSqueezyWebClient.get()
                .uri("/products")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray products = jsonResponse.getJSONArray("data");
        StringBuilder productDetails = new StringBuilder();

        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);
            String id = product.optString("id", "N/A");

            JSONObject attributes = product.getJSONObject("attributes");

            String largeThumbUrl = attributes.has("large_thumb_url") &&
                    !attributes.isNull("large_thumb_url")
                    ? attributes.optString("large_thumb_url", "N/A") : "N/A";

            String name = attributes.optString("name", "N/A");

            String description = attributes.optString("description", "N/A");
            String priceFormatted = attributes.optString("price_formatted", "N/A");
            productDetails.append("ID: ").append(id).append("\n")
                    .append("Name: ").append(name).append("\n")
                    .append("Description: ").append(description).append("\n")
                    .append("Price: ").append(priceFormatted).append("\n")
                    .append("Large Thumbnail URL: ").append(largeThumbUrl).append("\n").append("\n");
        }
        return productDetails.toString();
    }

    // إعادة تسمية الـ Bean هنا لتجنب التعارض مع الـ WebClient الموجود في SecurityConfig
    @Bean
    @Lazy
    public WebClient lemonSqueezyWebClient(@Value("${lemon.squeezy.api.key}") String apiKey) {
        return WebClient.builder()
                .baseUrl("https://api.lemonsqueezy.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)  // إضافة الـ API Key
                .build();
    }

    // طريقة لإنشاء عملية Checkout باستخدام ProductService
    public String createCheckout(String variantId, Integer userId) {
        Product product = productService.getProductByVariantId(variantId);  // استخدام ProductService
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));

        if (product == null) {
            throw new ApiException("Product not found");
        }

        // تكوين بيانات الـ Payload
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
            }""", storeId, variantId, userId);

        // استدعاء الـ API لإنشاء الـ Checkout
        String response = lemonSqueezyWebClient.post()
                .uri("/checkouts")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return checkoutService.createCheckout(variantId, userId);
    }
}

