package com.example.E_COMMERCE.Backend.ServiceImplementation;

import com.example.E_COMMERCE.Backend.Entity.Order;
import com.example.E_COMMERCE.Backend.Entity.Product;
import com.example.E_COMMERCE.Backend.Entity.ProductImage;
import com.example.E_COMMERCE.Backend.Entity.Vendor;
import com.example.E_COMMERCE.Backend.Repository.OrderRepository;
import com.example.E_COMMERCE.Backend.Repository.ProductImageRepository;
import com.example.E_COMMERCE.Backend.Repository.ProductRepository;
import com.example.E_COMMERCE.Backend.Repository.VendorRepository;
import com.example.E_COMMERCE.Backend.Service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class VendorServiceImplementation implements VendorService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public Product addProduct(Product product, String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null) {
            throw new RuntimeException("Vendor not found");
        }
        product.setSellerType("VENDOR");
        product.setSellerId(vendor.getVendorId());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product, String vendorEmail) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null || !existingProduct.getSellerType().equals("VENDOR") || !existingProduct.getSellerId().equals(vendor.getVendorId())) {
            throw new RuntimeException("Unauthorized to update this product");
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setStock(product.getStock());
        existingProduct.setAvailable(product.isAvailable());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long productId, String vendorEmail) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null || !existingProduct.getSellerType().equals("VENDOR") || !existingProduct.getSellerId().equals(vendor.getVendorId())) {
            throw new RuntimeException("Unauthorized to delete this product");
        }

        productRepository.delete(existingProduct);
    }

    @Override
    public List<Product> getMyProducts(String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null) {
            throw new RuntimeException("Vendor not found");
        }
        return productRepository.findBySellerTypeAndSellerId("VENDOR", vendor.getVendorId());
    }

    @Override
    public List<Order> getOrdersForMyProducts(String vendorEmail) {
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null) {
            throw new RuntimeException("Vendor not found");
        }
        return orderRepository.findBySellerTypeAndSellerId("VENDOR", vendor.getVendorId());
    }

    @Override
    public List<ProductImage> uploadImages(Long productId, List<MultipartFile> files, String vendorEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null || !product.getSellerType().equals("VENDOR") || !product.getSellerId().equals(vendor.getVendorId())) {
            throw new RuntimeException("Unauthorized to upload images for this product");
        }
        List<ProductImage> saved = new ArrayList<>();
        File dir = new File(uploadDir + File.separator + "products" + File.separator + productId);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (MultipartFile f : files) {
            if (f.isEmpty()) continue;
            String name = System.currentTimeMillis() + "_" + f.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            File dest = new File(dir, name);
            try {
                f.transferTo(dest);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file: " + f.getOriginalFilename());
            }
            ProductImage img = new ProductImage();
            img.setProduct(product);
            img.setUrl("/uploads/products/" + productId + "/" + name);
            saved.add(productImageRepository.save(img));
        }
        return saved;
    }
}
