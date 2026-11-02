package org.example.route;

import java.math.BigDecimal;

public interface ShippingCalculator {

    BigDecimal calculate(Governorate governorate);
}
