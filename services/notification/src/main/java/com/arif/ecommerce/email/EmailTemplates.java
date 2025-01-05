package com.arif.ecommerce.email;

import lombok.Getter;

public enum EmailTemplates {

    PAYMENT_CONFIRMATION("payment-confirmation.html", "Ödeme Başarıyla Alındı"),
    ORDER_CONFIRMATION("order-confirmation.html", "Sipariş Onayı")
    ;

    @Getter
    private final String template;
    @Getter
    private final String subject;


    EmailTemplates(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
