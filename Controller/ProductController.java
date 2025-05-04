/*
package com.example.demo.Controller;

import com.example.demo.Model.Product;
import com.example.demo.Service.CheckoutService;
import com.example.demo.Service.LemoneSqueesyService;
import com.example.demo.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final LemoneSqueesyService lemoneSqueesyService;
    private final CheckoutService checkoutService;


    */
/*@PostMapping("/add")
    public ResponseEntity addProduct(@RequestBody Product product){
        productService.addProduct(product);
        return ResponseEntity.status(200).body("added successfully");
    } *//*
@PostMapping("add")
    public ResponseEntity addProduct(@RequestBody Product product) {
        return ResponseEntity.status(200).body("added");
    }

    @GetMapping("/get")
    public ResponseEntity getProducts() {
        String products = lemoneSqueesyService.getProducts();
        return ResponseEntity.status(200).body(products);
    }

    @GetMapping("/checkout/{variantId}/{userId}")
    public ResponseEntity checkout(@PathVariable String variantId, @PathVariable Integer userId) {
        String url = checkoutService.createCheckout(variantId, userId);
        return ResponseEntity.status(200).body(url);
    }



    @DeleteMapping("delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable Integer id){
        productService.deleteProduct(id);
        return ResponseEntity.status(200).body("deleted Successfully");
    }






}
*/
package com.example.demo.Controller;

import com.example.demo.Service.ProductService;
import com.example.demo.Model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{variantId}")
    public Product getProduct(@PathVariable String variantId) {
        return productService.getProductByVariantId(variantId);
    }

    @PostMapping
    public ResponseEntity addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return ResponseEntity.status(200).body("added");
    }
}
