package org.example.route;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderRegistry {

    private final List<Order> orders = new ArrayList<>();

    public void save(Order order) {
        orders.add(order);
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }

    public String generateOrderNumber() {

        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        int sequence = orders.size() + 1;

        return String.format(date, sequence);
    }

    @PreDestroy
    public void printEndOfDaySummary() {

        BigDecimal revenue = BigDecimal.ZERO;

        for (Order order : orders) {
            revenue = revenue.add(order.total());
        }


        revenue = revenue.setScale(2, java.math.RoundingMode.HALF_UP);

        System.out.println("End of day: " + orders.size() + " orders, revenue " + revenue.toPlainString() + " EGP");
    }
}

