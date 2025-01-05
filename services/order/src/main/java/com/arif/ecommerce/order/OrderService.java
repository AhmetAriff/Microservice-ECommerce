package com.arif.ecommerce.order;

import com.arif.ecommerce.kafka.OrderConfirmation;
import com.arif.ecommerce.customer.CustomerClient;
import com.arif.ecommerce.exception.BusinessException;
import com.arif.ecommerce.kafka.OrderProducer;
import com.arif.ecommerce.orderline.OrderLineRequest;
import com.arif.ecommerce.orderline.OrderLineService;
import com.arif.ecommerce.payment.PaymentClient;
import com.arif.ecommerce.payment.PaymentRequest;
import com.arif.ecommerce.product.ProductClient;
import com.arif.ecommerce.product.PurchaseRequest;
import com.arif.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;



    @Transactional
    public Integer createOrder(OrderRequest request) {

        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        var purchasedProducts = productClient.purchaseProducts(request.products());

        BigDecimal totalAmount = calculateTotalAmount(request.products(), purchasedProducts);

        String reference = "ORDER-" + UUID.randomUUID().toString();

        var order = this.repository.save(mapper.toOrder(request, reference));

        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        var paymentRequest = new PaymentRequest(
                totalAmount,
                request.paymentMethod(),
                order.getId(),
                reference,
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        reference,
                        totalAmount,
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }

    private BigDecimal calculateTotalAmount(List<PurchaseRequest> purchaseRequests, List<PurchaseResponse> purchasedProducts) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (int i = 0; i < purchasedProducts.size(); i++) {
            var product = purchasedProducts.get(i);
            var quantity = purchaseRequests.get(i).quantity();
            totalAmount = totalAmount.add(product.price().multiply(BigDecimal.valueOf(quantity)));
        }

        return totalAmount;
    }
}
