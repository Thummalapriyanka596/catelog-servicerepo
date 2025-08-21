package com.example.catalog;

import com.example.catalog.Entity.Product;
import com.example.catalog.Exception.ProductNotFoundException;
import com.example.catalog.Repository.ProductRepository;
import com.example.catalog.Service.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.yml")
public class ProductServiceFullIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach

        void cleanDatabase()
        {
            productRepository.deleteAll();
        }

    // ✅ 1. Save Product - Positive
    @Test
    @Order(1)
    void testSaveProduct_success()
    {
        Product product=new Product(null,"priya",30.9);
        Product savedProduct=productService.saveProduct(product);
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("priya");
        assertThat(savedProduct.getPrice()).isEqualTo(30.9);
        assertThat(productRepository.count()).isEqualTo(1);
    }

    // ✅ 2. Get Product By ID - Positive
    @Test
    @Order(2)
    void testgetProductById_success()
    {
        Product product=new Product(null,"priya",30.9);
       // Here I use repository.save() because I’m not testing the save logic, I’m testing getProductById.
        Product savedProduct=productRepository.save(product);
        Product result=productService.getProductById(savedProduct.getId());
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("priya");

    }

    // ❌ 3. Get Product By ID - Not Found
    @Test
    @Order(3)
    void testgetProductById_notFound()
    {
        Product product=new Product(null,"priya",30.9);
        // Here I use repository.save() because I’m not testing the save logic, I’m testing getProductById.
        Product savedProduct=productRepository.save(product);
        assertThatThrownBy(()->productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class);

    }

    // ✅ 4. Get All Products
    @Test
    @Order(4)
    void testgetAllProducts_success()
    {
        productRepository.save(new Product(null, "A", 10.0));
        productRepository.save(new Product(null, "B", 20.0));

        List<Product> savedProduct=productService.getAllproducts();
        assertThat(savedProduct.size()).isEqualTo(2);
        assertThat(savedProduct.get(0).getName()).isEqualTo("A");

    }

    // ✅ 5. Update Product - Positive
    @Test
    @Order(5)
    void updateProductById_success()
    {
        Product product=new Product(null,"priya",30.9);
        Product savedProduct=productRepository.save(product);
        Product newProduct=new Product(savedProduct.getId(),"praneeth",39.9);
        Product updatedProduct=productService.updateProduct(savedProduct.getId(),newProduct);
        assertThat(updatedProduct.getName()).isEqualTo("praneeth");
        assertThat(updatedProduct.getPrice()).isEqualTo(39.9);

    }

    // ❌ 6. Update Product - Not Found
    @Test
    @Order(6)
    void updateProductById_notFound()
    {
        Product updateProduct=new Product(99L,"praneeth",39.9);
        assertThatThrownBy(()->productService.updateProduct(10L,updateProduct)).isInstanceOf(ProductNotFoundException.class);

    }

    // ✅ 7. Delete Product - Positive
    @Test
    @Order(7)
    void deleteProductById_success()
    {
        Product product=new Product(null,"priya",30.9);
        Product savedProduct=productRepository.save(product);
       productService.deleteProduct(savedProduct.getId());
        assertThat(productRepository.existsById(product.getId())).isFalse();

    }

    // ✅ 8. Delete Product - notFound
    @Test
    @Order(8)
    void deleteProductById_notFound()
    {
        Product product=new Product(null,"priya",30.9);
        Product savedProduct=productRepository.save(product);
        assertThatThrownBy(()->productService.deleteProduct(34L))
                .isInstanceOf(ProductNotFoundException.class);

    }
}
