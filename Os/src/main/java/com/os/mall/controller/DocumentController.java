package com.os.mall.controller;

import com.os.mall.entity.Good;
import com.os.mall.service.DocumentService;
import com.os.mall.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("saleadvice")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private GoodService goodService;

    @GetMapping("/advice")
    public String getSalesAdvice() {
        List<Good> lowSaleGoods = goodService.findLowSaleGoods(10L);  // 假设销量阈值为 10
        return documentService.generateSalesAdviceHtml(lowSaleGoods);
    }
    @GetMapping("/marketing-suggestions")
    public ResponseEntity<String> getMarketingSuggestions() {
        List<String> suggestions = goodService.generateMarketingSuggestions();
        StringBuilder htmlResponse = new StringBuilder("<html><body>");

        htmlResponse.append("<h2>销售建议</h2>");
        suggestions.forEach(suggestion -> {
            htmlResponse.append("<p>").append(suggestion).append("</p>");
        });
        htmlResponse.append("</body></html>");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlResponse.toString());
    }


}
