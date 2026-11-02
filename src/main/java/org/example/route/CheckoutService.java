package org.example.route;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final ProductService productService;
    private final ShippingQuoteService shippingQuoteService;
    private final TaxCalculator taxCalculator;
    private final OrderRegistry orderRegistry;
    private final List<NotificationSender> notificationSenders;

    public Order placeOrder(
            ShoppingCart cart,
            Governorate governorate) {

        if (cart.isEmpty()) {
            throw new StoreException(
                    "Cannot checkout an empty cart."
            );
        }

        Map<String, Integer> cartLines = cart.getLines();

        // Step 1: Validate every line before changing stock.
        for (Map.Entry<String, Integer> line
                : cartLines.entrySet()) {

            Product product =
                    productService.findBySku(line.getKey());

            if (line.getValue() > product.getStockQuantity()) {
                throw new StoreException(
                        "Insufficient stock for: "
                                + product.getName()
                );
            }
        }
        BigDecimal subtotal = cart.subtotal();

        BigDecimal vat =
                taxCalculator.calculateVAT(subtotal);

        BigDecimal shippingFee =
                shippingQuoteService.quote(
                        governorate,
                        subtotal
                );

        BigDecimal total = subtotal
                .add(vat)
                .add(shippingFee)
                .setScale(2, RoundingMode.HALF_UP);

        for (Map.Entry<String, Integer> line
                : cartLines.entrySet()) {

            productService.reduceStock(
                    line.getKey(),
                    line.getValue()
            );
        }
        String orderNumber =
                orderRegistry.generateOrderNumber();

        Order order = new Order(
                orderNumber,
                cart.getCustomer(),
                governorate,
                cartLines,
                subtotal,
                vat,
                shippingFee,
                total
        );

        orderRegistry.save(order);
        String message = String.format(
                "Order %s confirmed. Total %s EGP",
                orderNumber,
                total.toPlainString()
        );

        for (NotificationSender sender : notificationSenders) {
            sender.send(cart.getCustomer(), message);
        }
        cart.clear();

        return order;
    }
}

