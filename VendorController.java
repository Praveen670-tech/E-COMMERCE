package com.example.E_COMMERCE.Backend.Controller;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Product;
import com.example.E_COMMERCE.Backend.Entity.ProductImage;
import com.example.E_COMMERCE.Backend.Service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/vendor")
@PreAuthorize("hasRole('VENDOR')")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping("/product")
    public Product addProduct(@RequestBody Product product, Principal principal) {
        return vendorService.addProduct(product, principal.getName());
    }

    @PutMapping("/product/{productId}")
    public Product updateProduct(@PathVariable Long productId, @RequestBody Product product, Principal principal) {
        return vendorService.updateProduct(productId, product, principal.getName());
    }

    @DeleteMapping("/product/{productId}")
    public void deleteProduct(@PathVariable Long productId, Principal principal) {
        vendorService.deleteProduct(productId, principal.getName());
    }

    @GetMapping("/products")
    public List<Product> getMyProducts(Principal principal) {
        return vendorService.getMyProducts(principal.getName());
    }

    @GetMapping("/orders")
    public List<Order> getOrdersForMyProducts(Principal principal) {
        return vendorService.getOrdersForMyProducts(principal.getName());
    }

    @PostMapping("/product/{productId}/images")
    public List<ProductImage> uploadImages(@PathVariable Long productId,
                                           @RequestParam("files") List<MultipartFile> files,
                                           Principal principal) {
        return vendorService.uploadImages(productId, files, principal.getName());
    }
}
