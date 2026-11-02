package org.example.route;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryProductRepository
        implements ProductRepository {

    private final Map<String, Product> products =
            new LinkedHashMap<>();

    @PostConstruct
    public void loadProducts() {

        save(new Product(
                "PH-X5-128",
                "Smartphone X5 128GB",
                "Electronics",
                new BigDecimal("16000.00"),
                12
        ));

        save(new Product(
                "ACC-CHG-20W",
                "Fast Charger 20W",
                "Electronics",
                new BigDecimal("650.00"),
                40
        ));

        save(new Product(
                "ACC-BUDS-6",
                "Wireless Earbuds",
                "Electronics",
                new BigDecimal("1100.00"),
                8
        ));

        save(new Product(
                "FSH-GAL-001",
                "Cotton Galabeya",
                "Fashion",
                new BigDecimal("900.00"),
                25
        ));

        save(new Product(
                "HOM-PAN-28",
                "Non-stick Frying Pan 28cm",
                "Home & Kitchen",
                new BigDecimal("1200.00"),
                3
        ));

        save(new Product(
                "HOM-SPC-RCK",
                "Wooden Spice Rack",
                "Home & Kitchen",
                new BigDecimal("750.00"),
                10
        ));

        save(new Product(
                "BTY-SUN-50",
                "Sun Cream SPF50",
                "Beauty",
                new BigDecimal("350.00"),
                0
        ));

        save(new Product(
                "BK-CAI-TRI",
                "The Cairo Trilogy",
                "Books",
                new BigDecimal("450.00"),
                15
        ));

        System.out.println("Product catalog loaded.");
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return Optional.ofNullable(products.get(sku));
    }

    @Override
    public void save(Product product) {
        products.put(product.getSku(), product);
    }

    @PreDestroy
    public void closeCatalog() {
        System.out.println("Catalog closed");
    }
}