package com.arif.ecommerce.payment;

import com.arif.ecommerce.customer.CustomerResponse;
import com.arif.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    CustomerResponse customer
) {
}
