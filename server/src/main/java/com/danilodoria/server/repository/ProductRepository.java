package com.danilodoria.server.repository;

import com.danilodoria.server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByCategoryNameContainingIgnoreCase(String categoryName);
}
