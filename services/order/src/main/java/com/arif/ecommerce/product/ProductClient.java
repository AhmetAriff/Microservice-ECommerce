package com.arif.ecommerce.product;

import com.arif.ecommerce.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class ProductClient {

    @Value("${application.config.product-url}")
    private String productUrl;
    private final RestTemplate restTemplate;

    public List<PurchaseResponse> purchaseProducts(List<PurchaseRequest> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<List<PurchaseResponse>> responseType = new ParameterizedTypeReference<>() {
        };

        try {
            ResponseEntity<List<PurchaseResponse>> responseEntity = restTemplate.exchange(
                    productUrl + "/purchase",
                    POST,
                    requestEntity,
                    responseType
            );
            return responseEntity.getBody();
        } catch (HttpClientErrorException.BadRequest e) {
            String errorMessage = e.getResponseBodyAsString();
            throw new BusinessException("Ürün satın alma başarısız oldu: " + errorMessage);
        } catch (Exception e) {
            throw new BusinessException("Ürün satın alma sırasında bir hata oluştu.");
        }
    }

}
