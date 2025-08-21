package com.example.catalog.ServiceImpl;

import com.example.catalog.Entity.Product;
import com.example.catalog.Exception.ProductNotFoundException;
import com.example.catalog.Repository.ProductRepository;
import com.example.catalog.Service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    public ProductServiceImpl(ProductRepository productRepository)
    {
        this.productRepository=productRepository;
    }

    //To get all the products list
    @Override
    public List<Product> getAllproducts() {
        return productRepository.findAll();
    }

    //To save particular product
    @Override
    public Product saveProduct(Product product) {

        if (product == null) {
            throw new NullPointerException("Product cannot be null");
        }
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product not found with id: "+id));
    }

    @Override
    public Product updateProduct(Long id, Product product)  {
        Product existingProduct=getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        Product savedProduct=productRepository.save(existingProduct);
        return savedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        Product existingProduct=getProductById(id);
        productRepository.deleteById(id);
    }
}
