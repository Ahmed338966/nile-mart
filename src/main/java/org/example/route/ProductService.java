package org.example.route;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findBySku(String sku) {

        return productRepository.findBySku(sku)
                .orElseThrow(() ->
                        new StoreException(
                                "Product not found: " + sku
                        )
                );
    }

    public void reduceStock(String sku, int qty) {

        if (qty <= 0) {
            throw new StoreException(
                    "Quantity must be greater than zero."
            );
        }

        Product product = findBySku(sku);

        if (product.getStockQuantity() < qty) {
            throw new StoreException(
                    "Insufficient stock for: "
                            + product.getName()
            );
        }

        product.setStockQuantity(
                product.getStockQuantity() - qty
        );

        productRepository.save(product);
    }

}
