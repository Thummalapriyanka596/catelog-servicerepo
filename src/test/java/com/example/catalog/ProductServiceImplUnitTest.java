package com.example.catalog;

import com.example.catalog.Entity.Product;
import com.example.catalog.Exception.ProductNotFoundException;
import com.example.catalog.Repository.ProductRepository;
import com.example.catalog.ServiceImpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceImplUnitTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
         product=new Product(1L,"Test",90.0);

    }

    // if we do repo.save it will return product. it will not connect to db. just provided sample values it return
    @Test
    void saveProdcut_success()
    {

        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product savedProduct=productService.saveProduct(product);
        assertEquals("Test",savedProduct.getName());
        verify(productRepository,times(1)).save(product);
    }
    //When you pass a null DTO expects Nullpointer exception
    @Test
    void saveProduct_nullDto()
    {
        when(productRepository.save(any(Product.class))).thenReturn(null);
        Exception exception=assertThrows(NullPointerException.class,()->
                productService.saveProduct(null)
        );
        assertEquals("Product cannot be null", exception.getMessage());

        // ❌ Ensure repository.save() is never called
        verify(productRepository, never()).save(any());

    }

    //Test getting all the products or not
    @Test
    void getAllProducts_success()
    {
        List<Product> products= Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);
        List<Product> productList=productService.getAllproducts();
        assertEquals(1,productList.size());
        verify(productRepository,times(1)).findAll();
    }
    @Test
    void getProductById_success() throws ProductNotFoundException
    {
        //"When the findById(1L) method is called on productRepository, pretend it returns a Product wrapped inside an Optional."
         when(productRepository.findById(1L)).thenReturn(Optional.of(product));
         Product result=productService.getProductById(1L);
         assertNotNull(result);
         assertEquals("Test",result.getName());

    }
    //When the provioded id not present returnts runtime exception
    @Test
    void getProductById_notFound()
    {

        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,()->

                    productService.getProductById(2L)
                );

    }

    //  // ✅ Update Product - Found
    // When the findById(1L) method is called on productRepository, pretend it returns a Product wrapped inside an Optional.
    @Test
    void updateProduct_Found() throws ProductNotFoundException
    {
        Product updatedProduct=new Product(2L,"Phone",66.9);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        Product savedProduct=productService.updateProduct(1L,updatedProduct);
        assertEquals("Phone",savedProduct.getName());
    }

    //We're calling the actual method under test:
    //productService.updateProduct(1L, updatedDTO)
    //
    //Since the product was not found (because findById() returned Optional.empty()), we expect it to throw a RuntimeException.

    @Test
    void testUpdateProduct_notFound()
    {
        Product updatedProduct=new Product(2L,"Phone",66.9);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,()->
        {
            productService.updateProduct(1L,updatedProduct);
        }
                );

    }

    //    // ✅ Delete Product - Success
    @Test
    void testDeleteProductById_success()
    {
        Product product=new Product(3L,"Tea",300.9);
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(3L);
        productService.deleteProduct(3L);
        verify(productRepository,times(1)).findById(3L);
        verify(productRepository,times(1)).deleteById(3L);

    }

    //    // ✅ Delete Product - notFound
    @Test
    void testDeleteProductById_notFound()
    {
        Product product=new Product(3L,"Tea",300.9);
        when(productRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,()->
                productService.deleteProduct(3L)
                );

    }
}
