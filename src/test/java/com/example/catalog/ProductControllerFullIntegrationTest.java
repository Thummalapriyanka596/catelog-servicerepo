package com.example.catalog;

import com.example.catalog.Entity.Product;
import com.example.catalog.Repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ActiveProfiles("test")  we have disable this since we have configured h2 config in app.yml
@SpringBootTest // Loads full context
@AutoConfigureMockMvc // Allows MockMvc injection
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //To follow order
//@Transactional // Rollback DB after each test
@TestPropertySource(locations = "classpath:application-test.yml")
public class ProductControllerFullIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
    }


    @Test
    @Order(1)
    @Commit
    void testSaveProduct_success() throws Exception {
        Product product = new Product(null, "Laptop", 1200.00);
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1200.00));

                // Verify DB contains the product
                assert productRepository.findAll().size()==1;
       // Thread.sleep(60000);


    }
    // ❌ 2. Save Product - Missing name
    @Test
    @Order(2)
    void testSaveProduct_missingName() throws Exception {
        Product product = new Product(null, null, 1200.00);
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isBadRequest());
    }

    // ✅ 3. Get All Products
    @Test
    @Order(3)
    void getAllProducts_success() throws Exception {
        productRepository.save(new Product(null,"priya",10.8));
        productRepository.save(new Product(null,"praneeth",30.9));
        mockMvc.perform(get("/getAllProducts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("priya"));
    }

    // ✅ 4. Get Product By ID - Found
    @Test
    @Order(4)
    void getProductById_success() throws Exception {
        Product savedProduct=productRepository.save(new Product(null,"priya",10.8));
        mockMvc.perform(get("/getProduct/{id}",savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("priya"));
    }
    // ❌ 5. Get Product By ID - Not Found
    @Test
    @Order(5)
    void getProductById_notFound() throws Exception {
        Product savedProduct=productRepository.save(new Product(null,"priya",10.8));
        mockMvc.perform(get("/getProduct/{id}",99))
                .andExpect(status().isNotFound());
    }


    // ✅ 6. Update Product - Found
    @Test
    @Order(6)
    void updateProductById_found() throws Exception
    {
        Product product=new Product(null,"priya",30.0);
        Product savedProduct=productRepository.save(product);
        Product updateProduct=new Product(savedProduct.getId(),"praneeth",70.9);
        mockMvc.perform(put("/updateProduct/{id}",savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("praneeth"))
                .andExpect(jsonPath("$.price").value(70.9));

    }

    // ❌ 7. Update Product - Not Found
    @Test
    @Order(7)
    void updateProductById_notFound() throws Exception
    {
        Product product=new Product(null,"priya",30.0);
        Product savedProduct=productRepository.save(product);
        Product updateProduct=new Product(99L,"praneeth",70.9);
        mockMvc.perform(put("/updateProduct/{id}",99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateProduct)))
                .andExpect(status().isNotFound());


    }
    // ❌ 8. Delete Product -  Found
    @Test
    @Order(8)
    void deleteProductById_found() throws Exception
    {
        Product product=new Product(null,"priya",30.0);
        Product savedProduct=productRepository.save(product);
        mockMvc.perform(delete("/deleteProduct/{id}",savedProduct.getId()))
                .andExpect(status().isNoContent());
        assert productRepository.findById(savedProduct.getId()).isEmpty();

    }

    // ❌ 9. Delete Product -  not Found
    @Test
    @Order(9)
    void deleteProductById_notFound() throws Exception
    {
        Product product=new Product(null,"priya",30.0);
        Product savedProduct=productRepository.save(product);
        mockMvc.perform(delete("/deleteProduct/{id}",99))
                .andExpect(status().isNotFound());

    }
}
