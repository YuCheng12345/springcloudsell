package com.yc.controller;

import com.yc.dataobject.ProductInfo;
import com.yc.dto.CartDTO;
import com.yc.product.client.ProductClient;
import com.yc.product.common.DecreaseStockInput;
import com.yc.product.common.ProductInfoOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class ClientController {

    @Autowired
    private ProductClient productClient;

    @GetMapping("/getProductList")
    public String getProductList() {

        List<ProductInfoOutput> productInfoList = productClient.listForOrder(Arrays.asList("12358"));
        log.info("response={}", productInfoList);

        return "ok";
    }

    @GetMapping("/getDecreaseStock")
    public String decreaseStock() {

        productClient.decreaseStock(Arrays.asList(new DecreaseStockInput("12358", 8)));

        return "ok";
    }


}
