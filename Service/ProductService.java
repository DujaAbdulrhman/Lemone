/*
package com.example.demo.Service;

import com.example.demo.API.ApiException;
import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final LemoneSqueesyService lemoneSqueesyService;


    @PostConstruct
    public void init() {
        // نفذ العمليات التي تعتمد على LemoneSqueesyService هنا
        lemoneSqueesyService.initService(); // إذا كانت هناك عمليات تحتاج إلى التنفيذ بعد تهيئة الخدمة
    }

    public void addProduct(Product product){
        productRepository.save(product);
    }

    public List getall(){
        return productRepository.findAll();
    }

    public Product findByVariantId(String variantId) {
        return productRepository.findProductByVariantId(variantId);
    }

    public void deleteProduct(Integer id){
        Product product=productRepository.getReferenceById(id);
        if (product==null){
            throw new ApiException("product not found");
        }
        productRepository.delete(product);
    }


    */
/*public Product getProductByVariantId(String variantId) {
    }*//*

}
*/

package com.example.demo.Service;

import com.example.demo.API.ApiException;
import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;


    public void addProduct(Product product){
        productRepository.save(product);
    }

    // استرجاع منتج باستخدام معرف الـ variantId
    public Product getProductByVariantId(String variantId) {
        return productRepository.findByVariantId(variantId);
    }
}
