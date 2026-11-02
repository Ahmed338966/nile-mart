package org.example.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class ShoppingCart {

    private final ProductService productService;
    private final int maxQuantityPerProduct;

    private int cartNumber;
    private Customer customer;

    private final Map<String, Integer> lines =
            new LinkedHashMap<>();

    public ShoppingCart(
            ProductService productService,
            @Value("${store.max-quantity-per-product}")
            int maxQuantityPerProduct) {

        this.productService = productService;
        this.maxQuantityPerProduct = maxQuantityPerProduct;
    }

    public void initialize(int cartNumber, Customer customer) {
        this.cartNumber = cartNumber;
        this.customer = customer;
    }

    public void addItem(String sku, int qty) {

        if (qty <= 0) {
            throw new StoreException(
                    "Quantity must be greater than zero."
            );
        }

        Product product = productService.findBySku(sku);

        if (product.getStockQuantity() == 0) {
            throw new StoreException(
                    "Product is out of stock: " + product.getName()
            );
        }

        int currentQuantity = lines.getOrDefault(sku, 0);
        int newQuantity = currentQuantity + qty;

        if (newQuantity > maxQuantityPerProduct) {
            throw new StoreException(
                    "Maximum quantity per product is "
                            + maxQuantityPerProduct
            );
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new StoreException(
                    "Insufficient stock for: " + product.getName()
            );
        }

        lines.put(sku, newQuantity);
    }

    public BigDecimal subtotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> line : lines.entrySet()) {

            Product product =
                    productService.findBySku(line.getKey());

            BigDecimal linePrice = product.getPrice()
                    .multiply(BigDecimal.valueOf(line.getValue()));

            total = total.add(linePrice);
        }

        return total.setScale(
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void clear() {
        lines.clear();
    }

    public int getCartNumber() {
        return cartNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Map<String, Integer> getLines() {
        return new LinkedHashMap<>(lines);
    }
}

