package com.yc.service.impl;

import com.yc.dataobject.OrderDetail;
import com.yc.dataobject.OrderMaster;
import com.yc.dto.OrderDTO;
import com.yc.enums.OrderStatusEnum;
import com.yc.enums.PayStatusEnum;
import com.yc.product.client.ProductClient;
import com.yc.product.common.DecreaseStockInput;
import com.yc.product.common.ProductInfoOutput;
import com.yc.reprository.OrderDetailRepository;
import com.yc.reprository.OrderMasterRepository;
import com.yc.service.OrderService;
import com.yc.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductClient productClient;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        String orderId = KeyUtil.genUniqueKey();


        List<String> productIdList = orderDTO.getOrderDetailList().stream().map(OrderDetail::getProductId).collect(Collectors.toList());
        //1.查询商品（数量，价格）
        List<ProductInfoOutput> productInfoList = productClient.listForOrder(productIdList);
        //2.计算订单总价
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        for(OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            for(ProductInfoOutput productInfo: productInfoList){
                if(productInfo.getProductId().equals(orderDetail.getProductId())){
                    orderAmount = productInfo.getProductPrice()
                            .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                            .add(orderAmount);
                    //订单详情入库
                    BeanUtils.copyProperties(productInfo, orderDetail);
                    orderDetail.setDetailId(KeyUtil.genUniqueKey());
                    orderDetail.setOrderId(orderId);
                    orderDetailRepository.save(orderDetail);

                }
            }

        }
        //TODO 4.扣库存
        List<DecreaseStockInput> decreaseStockList = orderDTO.getOrderDetailList().stream()
                .map(e -> new DecreaseStockInput(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productClient.decreaseStock(decreaseStockList);

        //3. 写入订单数据库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);

        return orderDTO;
    }


}
