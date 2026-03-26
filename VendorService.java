package com.example.E_COMMERCE.Backend.Service;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Product;

import java.util.List;

public interface VendorService {

    Product addProduct(Product product, String vendorEmail);

    Product updateProduct(Long productId, Product product, String vendorEmail);

    void deleteProduct(Long productId, String vendorEmail);

    List<Product> getMyProducts(String vendorEmail);

    List<Order> getOrdersForMyProducts(String vendorEmail);

    java.util.List<com.example.E_COMMERCE.Backend.Entity.ProductImage> uploadImages(Long productId, java.util.List<org.springframework.web.multipart.MultipartFile> files, String vendorEmail);
}
