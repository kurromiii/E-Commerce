package com.mftplus.ecommerce.repository;

import com.mftplus.ecommerce.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBrandName(String name);

    List<Product> findByBrandNameAndCategoriesName(String brandName, String categoryName);

    List<Product> findByCategoriesName(String name);
}
