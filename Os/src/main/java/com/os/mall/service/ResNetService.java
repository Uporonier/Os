package com.os.mall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ResNetService {

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public ResNetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Set<Integer> getRecommendations(List<Long> productIds) {
        Set<Integer> recommendations = new HashSet<>();
        for (Long productId : productIds) {
            String url = String.format("%s/get_similar_products?product_id=%d", pythonServiceUrl, productId);
            try {
                List<Integer> response = restTemplate.getForObject(url, List.class); // Assuming the response is a list of product IDs
                if (response != null) {
                    recommendations.addAll(response);
                }
                else
                {
                    System.out.println("response 是空");
                }
            } catch (Exception e) {
                // Handle exceptions or log errors as appropriate
                System.out.println("Error retrieving recommendations for product ID " + productId + ": " + e.getMessage());
            }
        }
//        System.out.println(recommendations);
        return recommendations;
    }
}
