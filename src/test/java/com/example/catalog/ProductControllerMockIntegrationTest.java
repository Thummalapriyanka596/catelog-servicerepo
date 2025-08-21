package com.example.catalog;

import com.example.catalog.Controller.ProductController;
import com.example.catalog.DTO.ProductDTO;
import com.example.catalog.Entity.Product;
import com.example.catalog.Exception.ProductNotFoundException;
import com.example.catalog.Service.ProductService;
import com.example.catalog.ServiceImpl.ProductServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//We were mainly verifying:Endpoints mapping,Status codes,JSON response body
//That’s why we injected @MockBean ProductService — it gave predictable responses without hitting the DB.
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerMockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;;
    // ✅ 1. Save Product - Positive
    @Test
    @Order(1)
    void testSaveProduct_success() throws Exception {
        ProductDTO productDto=new ProductDTO(null,"Sample1",78.9);
        Product savedProduct=new Product(1L,"Sample1",78.9);
        when(productService.saveProduct(any(Product.class))).thenReturn(savedProduct);
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sample1"))
                .andExpect(jsonPath("$.price").value(78.9));
    }

    // ❌ Save Product - Negative (Null DTO)
    @Test
    @Order(2)
    void testSaveProduct_nullDTO() throws Exception {
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))  //Empty i/p
                .andExpect(status().isBadRequest());
    }

    // // ❌ Save Product - Negative (Empty name)
    @Test
    @Order(3)
    void testSaveProduct_emptyName() throws Exception {
        ProductDTO productDTO=new ProductDTO(null,null,78.9);
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest());
    }

    // ✅ 2. Get Product by Id - Positive
    @Test
    @Order(4)
    void test_getProductById_positive() throws Exception {
        Product product=new Product(1L,"Phone",78.9);
        when(productService.getProductById(1L)).thenReturn(product);
        mockMvc.perform(get("/getProduct/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    // . Get Product by Id - Positive
    @Test
    @Order(5)
    void test_getProductById_negative() throws Exception {
        Product product=new Product(1L,"Phone",78.9);
        when(productService.getProductById(99L)).thenThrow(ProductNotFoundException.class);
        mockMvc.perform(get("/getProduct/99"))
                .andExpect(status().isNotFound());

    }

    // ✅ 3. Get All Products by Id - Positive
    @Test
    @Order(6)
    void test_getAllProductById_positive() throws Exception {
        List<Product> productList=List.of(
                new Product(1L,"ProductA",40.9),
                new Product(2L,"ProductB",23.9)
        );
        when(productService.getAllproducts()).thenReturn(productList);
        mockMvc.perform(get("/getAllProducts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("ProductA"));
    }

    // ✅ 4. Update broduct - Positive
    @Test
    @Order(7)
    void test_updateProduct_positive() throws Exception {
        Product updatedProduct=new Product(1L,"ProductA",40.9);
        Product product=new Product(1L,"ProductA",40.9);
        when(productService.updateProduct(eq(1L),any(Product.class))).thenReturn(product);
        MvcResult result=mockMvc.perform(put("/updateProduct/{id}",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andReturn();
                //.andExpect(jsonPath("$.name").value("ProductA"));
        //To print result on console
        System.out.println("RESPONSE: " + result.getResponse().getContentAsString());
    }

    //  Update product - invalidId
    @Test
    @Order(8)
    void testUpdateProduct_inValidId() throws Exception {
        Product updatedProduct=new Product(1L,"ProductA",40.9);
        Product product=new Product(1L,"ProductA",40.9);
        when(productService.updateProduct(eq(90L),any(Product.class))).thenThrow(ProductNotFoundException.class);
        mockMvc.perform(put("/updateProduct/{id}",90L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound());

    }
    //5. Delete Product by id - positive
    @Test
    @Order(9)
    void testdeleteProduct_success() throws Exception {
        Product product=new Product(1L,"ProductA",40.9);
        mockMvc.perform(delete("/deleteProduct/{id}",1))
                .andExpect(status().isNoContent());
    }

    // Delete Product by id - negative
    @Test
    @Order(10)
    void testdeleteProduct_fail() throws Exception {
        doThrow(new ProductNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(99L);
        Product product=new Product(1L,"ProductA",40.9);
        mockMvc.perform(delete("/deleteProduct/{id}",99))
                .andExpect(status().isNotFound());
    }
}


