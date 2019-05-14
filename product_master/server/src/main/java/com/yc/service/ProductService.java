package com.yc.service;

import com.yc.product.common.DecreaseStockInput;
import com.yc.dataobject.ProductInfo;

import java.util.List;

public interface ProductService {


    /**
     * 查询上架商品列表
     * @return
     */
    List<ProductInfo> findUpAll();


    /**
     * 查询商品列表
     * @param productIdList
     * @return
     */
    List<ProductInfo> findList(List<String> productIdList);


    /**
     * 减库存
     * @param decreaseStockInputList
     */
    void decreaseStock(List<DecreaseStockInput> decreaseStockInputList);
}














