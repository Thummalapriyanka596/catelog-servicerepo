package com.example.catalog;

import com.example.catalog.Controller.ProductController;
import com.example.catalog.DTO.ProductDTO;
import com.example.catalog.Entity.Product;
import com.example.catalog.Service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(ProductController.class) // ✅ Tells Spring to load only the Controller layer
public class ProductControllerUnitTest {


    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    private Product product;

    private  ProductDTO dto;
    @BeforeEach
    void setUp()
    {
        //To initialize Mock and InjectMock
        MockitoAnnotations.openMocks(this);
        product=new Product(1L,"Test",90.0);
         dto = new ProductDTO(null, "Test", 90.0);
    }

    @Test
    void testCreateProduct_success() throws Exception {
        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        //mockMvc.perform(...) simulates the POST request.
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)

                        //ObjectMapper	A class from Jackson (used in Spring Boot) that converts Java objects to JSON and vice versa.
                        //.writeValueAsString(dto)	Converts the dto (Java object) into a JSON string — exactly what an HTTP request would send.
                        //.content(...)	Adds this JSON string as the request body in the simulated HTTP POST call.
                                .content(new ObjectMapper().writeValueAsString(dto)))
                //status().isCreated() checks for HTTP 201.
                .andExpect(status().isCreated())

                //jsonPath(...) verifies that returned JSON has correct values.
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    // ❌ Negative Test: Missing name
    @Test
    void testCreateProduct_fail() throws Exception {
        dto=new ProductDTO(3L,"",40.0);
        //🔴 The @Valid validation fails before the productService.saveProduct(...) method is ever called.
        // when(productService.saveProduct(any(Product.class)))
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }
    // ❌ Negative Test: null DTO
    @Test
    void testCreateProduct_nullDto() throws Exception {
        mockMvc.perform(post("/saveProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllProducts_Success() throws Exception {
        List<Product> productList=List.of(
                new Product(1L,"ProductA",40.9),
                new Product(2L,"ProductB",23.9)
        );
        when(productService.getAllproducts()).thenReturn(productList);
        mockMvc.perform(get("/getAllProducts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("ProductA"));
    }

    // ✅ 2. Get Product By ID (Positive)
    @Test
    void testGetProductById_success() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product);
        mockMvc.perform(get("/getProduct/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));

    }

    // Get Product By ID (Negative)
    @Test
    void testGetProductById_negative() throws Exception {
        when(productService.getProductById(2L)).thenThrow(new RuntimeException("Product not found"));
        mockMvc.perform(get("/getProduct/2"))
                .andExpect(status().isInternalServerError());


    }

    // Update Product By ID (positive)
    @Test
    void testUpdateProductById_positive() throws Exception {
        ProductDTO updateProduct;
        Product product=new Product(1L,"Laptop max",30.9);
         updateProduct=new ProductDTO(1L,"Laptop max",30.9);
        when(productService.updateProduct(eq(1L),any(Product.class))).thenReturn(product);
        mockMvc.perform(put("/updateProduct/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop max"));

    }

    // Update Product By ID (negative)
    @Test
    void testUpdateProductById_negative() throws Exception {
        ProductDTO updateProduct;
        Product product=new Product(1L,"Laptop max",30.9);
        updateProduct=new ProductDTO(1L,"Laptop max",30.9);
        when(productService.updateProduct(eq(1L),any(Product.class))).thenThrow(new RuntimeException("Product not found"));
        mockMvc.perform(put("/updateProduct/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


    }

    // Delete Product By ID (positive)
    @Test
    void testDeleteProductById_positive() throws Exception {
       mockMvc.perform(delete("/deleteProduct/1"))
               .andExpect(status().isNoContent());
    }

    // Delete Product By ID (negative)
    @Test
    void testDeleteProductById_negative()  throws Exception{
        doThrow(new RuntimeException("Product not found") )
                .when(productService).deleteProduct(1L);
        mockMvc.perform(delete("/deleteProduct/1"))
                .andExpect(status().isInternalServerError());
    }
}
