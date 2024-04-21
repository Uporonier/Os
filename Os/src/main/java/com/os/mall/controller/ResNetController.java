package com.os.mall.controller;

import com.os.mall.service.RecommendationService;
import com.os.mall.service.UserEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/recommendations")
public class ResNetController {

    @Autowired
    private RecommendationService recommendationService;

//    @GetMapping("/getUserRecommendations")
//    public ResponseEntity<Set<Integer>> getUserRecommendations(@RequestParam("userId") Long userId) {
//        Set<Integer> recommendations = recommendationService.getUserRecommendations(userId);
//        return ResponseEntity.ok(recommendations);
//    }
}
