package com.danilodoria.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    /**
     * Carga perezosa (FetchType.LAZY): Por defecto, las relaciones @ManyToOne en JPA utilizan
     * FetchType.EAGER (traen la categoría automáticamente con cada consulta).
     * Es una buena práctica cambiarlo a LAZY para evitar problemas de
     * rendimiento (consultas innecesarias a la base de datos).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
