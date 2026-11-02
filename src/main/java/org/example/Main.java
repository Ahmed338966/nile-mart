package org.example;
import org.example.route.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(
                             AppConfig.class)) {

            Scanner scanner = new Scanner(System.in);

            ProductService productService =
                    context.getBean(ProductService.class);

            CheckoutService checkoutService =
                    context.getBean(CheckoutService.class);

            Map<Integer, ShoppingCart> carts =
                    new LinkedHashMap<>();

            int nextCartNumber = 1;
            boolean running = true;

            while (running) {
                System.out.println("1. Show products");
                System.out.println("2. Create a cart");
                System.out.println("3. Add a product to a cart");
                System.out.println("4. Show a cart");
                System.out.println("5. Check out a cart");
                System.out.println("0. Exit");
                System.out.print("Choose: ");

                String option = scanner.nextLine().trim();

                try {

                    switch (option) {

                        case "1" -> {
                            for (Product product
                                    : productService.findAll()) {

                                System.out.println(
                                        product.getSku()
                                                + " | "
                                                + product.getName()
                                                + " | "
                                                + product.getPrice()
                                                + " EGP | Stock: "
                                                + product.getStockQuantity()
                                );
                            }
                        }

                        case "2" -> {

                            System.out.print("Customer name: ");
                            String name = scanner.nextLine();

                            System.out.print("Phone: ");
                            String phone = scanner.nextLine();

                            System.out.print(
                                    "Email (optional): "
                            );
                            String email = scanner.nextLine();

                            Customer customer = new Customer(
                                    name,
                                    phone,
                                    email.isBlank() ? null : email
                            );

                            ShoppingCart cart =
                                    context.getBean(ShoppingCart.class);

                            cart.initialize(
                                    nextCartNumber,
                                    customer
                            );

                            carts.put(nextCartNumber, cart);

                            System.out.println(
                                    "Cart created. Cart number: "
                                            + nextCartNumber
                            );

                            nextCartNumber++;
                        }

                        case "3" -> {

                            System.out.print("Cart number: ");
                            int cartNumber = Integer.parseInt(
                                    scanner.nextLine().trim()
                            );

                            ShoppingCart cart =
                                    getCart(carts, cartNumber);

                            System.out.print("Product SKU: ");
                            String sku = scanner.nextLine().trim();

                            System.out.print("Quantity: ");
                            int qty = Integer.parseInt(
                                    scanner.nextLine().trim()
                            );

                            cart.addItem(sku, qty);

                            System.out.println(
                                    "Product added successfully."
                            );
                        }

                        case "4" -> {

                            System.out.print("Cart number: ");
                            int cartNumber = Integer.parseInt(
                                    scanner.nextLine().trim()
                            );

                            ShoppingCart cart =
                                    getCart(carts, cartNumber);

                            if (cart.isEmpty()) {
                                System.out.println("Cart is empty.");
                                break;
                            }

                            for (Map.Entry<String, Integer> line
                                    : cart.getLines().entrySet()) {

                                Product product =
                                        productService.findBySku(
                                                line.getKey()
                                        );

                                System.out.println(
                                        product.getName()
                                                + " | Quantity: "
                                                + line.getValue()
                                );
                            }

                            System.out.println(
                                    "Subtotal: "
                                            + cart.subtotal()
                                            + " EGP"
                            );
                        }

                        case "5" -> {

                            System.out.print("Cart number: ");
                            int cartNumber = Integer.parseInt(
                                    scanner.nextLine().trim()
                            );

                            ShoppingCart cart =
                                    getCart(carts, cartNumber);

                            System.out.println(
                                    "Available governorates:"
                            );

                            for (Governorate governorate
                                    : Governorate.values()) {
                                System.out.println(governorate.name());
                            }

                            System.out.print(
                                    "Enter governorate: "
                            );

                            String input =
                                    scanner.nextLine()
                                            .trim()
                                            .toUpperCase()
                                            .replace(' ', '_');

                            Governorate governorate =
                                    Governorate.valueOf(input);

                            Order order =
                                    checkoutService.placeOrder(
                                            cart,
                                            governorate
                                    );

                            System.out.println(
                                    "\nOrder completed successfully!"
                            );

                            System.out.println(
                                    "Order number: "
                                            + order.orderNumber()
                            );

                            System.out.println(
                                    "Subtotal: "
                                            + order.subtotal()
                            );

                            System.out.println(
                                    "VAT: " + order.vat()
                            );

                            System.out.println(
                                    "Shipping: "
                                            + order.shippingFee()
                            );

                            System.out.println(
                                    "Total: " + order.total()
                                            + " EGP"
                            );
                        }

                        case "0" -> {
                            running = false;
                            System.out.println("Exiting NileMart...");
                        }

                        default -> System.out.println(
                                "Invalid menu option."
                        );
                    }

                } catch (StoreException e) {

                    System.out.println(
                            "Store error: " + e.getMessage()
                    );

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Invalid input. Check the entered values."
                    );
                }
            }
        }
    }

    private static ShoppingCart getCart(
            Map<Integer, ShoppingCart> carts,
            int cartNumber) {

        ShoppingCart cart = carts.get(cartNumber);

        if (cart == null) {
            throw new StoreException(
                    "Cart not found: " + cartNumber
            );
        }

        return cart;
    }
}
