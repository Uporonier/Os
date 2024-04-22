package com.os.mall.service;

import com.os.mall.entity.Good;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DocumentService {

    public String generateSalesAdviceHtml(List<Good> lowSaleGoods) {
        StringBuilder html = new StringBuilder();
        html.append("<div>");
        html.append("<h1>销售建议报告</h1>");
        if (lowSaleGoods.isEmpty()) {
            html.append("<p>目前没有低销量的商品。</p>");
        } else {
            for (Good good : lowSaleGoods) {
                html.append("<p>");
                html.append("<strong>商品名称:</strong> ").append(good.getName());
                html.append(", <strong>销量:</strong> ").append(good.getSales());
                html.append("</p>");
            }
        }
        html.append("</div>");
        return html.toString();
    }

}
