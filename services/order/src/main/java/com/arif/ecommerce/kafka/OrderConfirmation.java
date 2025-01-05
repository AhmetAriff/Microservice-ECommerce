package com.arif.ecommerce.kafka;

import com.arif.ecommerce.customer.CustomerResponse;
import com.arif.ecommerce.order.PaymentMethod;
import com.arif.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
