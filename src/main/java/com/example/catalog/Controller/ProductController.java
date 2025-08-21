package com.example.catalog.Controller;


import com.example.catalog.DTO.ProductDTO;
import com.example.catalog.Entity.Product;
import com.example.catalog.Exception.ProductNotFoundException;
import com.example.catalog.Service.ProductService;
import com.example.catalog.ServiceImpl.ProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    private  ProductService productService;
    public ProductController(ProductService productService)

    {
        this.productService=productService;
    }

    private Product dtoToEntity(ProductDTO productDTO)
    {
        Product product=new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        return product;
    }




    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllproducts()
    {

        return new ResponseEntity<>(productService.getAllproducts(),HttpStatus.OK);
    }

   //Below is the normal createProduct API
   /*  @PostMapping("/saveProduct")
    public Product createProduct(@RequestBody Product product)
    {
        return productService.saveProduct(product);
    } */

    //Here changing createProduct to use validations
    @PostMapping("/saveProduct")
    public ResponseEntity<?> createProduct(@Valid  @RequestBody ProductDTO productDTO)
    {
        if(productDTO==null)
        {
            throw  new NullPointerException("Product cannot be null");
        }
        Product product=dtoToEntity(productDTO);
        Product savedProduct=productService.saveProduct(product);
        //productDTO savedDto=EntityTo
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }





    @GetMapping("/getProduct/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws ProductNotFoundException {
        return new ResponseEntity<>(productService.getProductById(id),HttpStatus.OK);
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<Product> updateProductById(@PathVariable Long id,@RequestBody Product newProduct)
    {
        return new ResponseEntity<>(productService.updateProduct(id,newProduct),HttpStatus.OK);
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable Long id)
    {
        productService.deleteProduct(id);
        //return  ResponseEntity.ok("Product has been deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


