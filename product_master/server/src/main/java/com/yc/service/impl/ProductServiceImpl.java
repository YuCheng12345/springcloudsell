package com.yc.service.impl;

import com.yc.product.common.DecreaseStockInput;
import com.yc.dataobject.ProductInfo;
import com.yc.enums.ProductStatusEnum;
import com.yc.enums.ResultEnum;
import com.yc.exception.ProductException;
import com.yc.repository.ProductInfoRepository;
import com.yc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository reprository;



    @Override
    public List<ProductInfo> findUpAll() {
        return reprository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public List<ProductInfo> findList(List<String> productIdList) {
        return reprository.findByProductIdIn(productIdList);
    }

    @Override
    @Transactional
    public void decreaseStock(List<DecreaseStockInput> decreaseStockInputList) {
        for(DecreaseStockInput decreaseStockInput : decreaseStockInputList){
            Optional<ProductInfo> productInfoOptional = reprository.findById(decreaseStockInput.getProductId());
            //判断商品是否存在
            if(!productInfoOptional.isPresent()){
                throw new ProductException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            ProductInfo productInfo = productInfoOptional.get();
            Integer result = productInfo.getProductStock()-decreaseStockInput.getProductQuantity();
            //库存是否足够
            if(result<0){
                throw new ProductException(ResultEnum.PRODUCT_STOCK_ERROR);

            }
            productInfo.setProductStock(result);

            reprository.save(productInfo);
        }
    }


}
