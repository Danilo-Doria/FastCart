package com.danilodoria.server.repository;

import com.danilodoria.server.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByNameContainingIgnoreCase(String nombre);

}
