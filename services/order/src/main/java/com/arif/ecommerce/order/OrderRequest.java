package com.arif.ecommerce.order;

import com.arif.ecommerce.product.PurchaseRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public record OrderRequest(
    Integer id,
    @NotNull(message = "Payment method should be precised")
    PaymentMethod paymentMethod,
    @NotNull(message = "Customer should be present")
    @NotEmpty(message = "Customer should be present")
    @NotBlank(message = "Customer should be present")
    String customerId,
    @NotEmpty(message = "You should at least purchase one product")
    List<PurchaseRequest> products
) {

}
