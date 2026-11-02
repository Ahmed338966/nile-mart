package org.example.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String sku;
    private String name;
    private String category;
    private BigDecimal price;
    private int stockQuantity;
}
