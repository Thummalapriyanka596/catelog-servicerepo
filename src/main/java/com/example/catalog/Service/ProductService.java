
package com.example.catalog.Service;

        import com.example.catalog.Entity.Product;
        import com.example.catalog.Exception.ProductNotFoundException;

        import java.util.List;

public interface ProductService {

    //To get all the products list
    List<Product> getAllproducts();
    Product saveProduct(Product product);
    Product getProductById(Long id) throws ProductNotFoundException;
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}
