package com.yc.controller;


import com.yc.exception.OrderException;
import com.yc.VO.ResultVO;
import com.yc.converter.OrderForm2OrderDTOConverter;
import com.yc.dto.OrderDTO;
import com.yc.enums.ResultEnum;
import com.yc.form.OrderForm;
import com.yc.service.OrderService;
import com.yc.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 1. 参数检验
     * 2. 查询商品信息（调用商品服务）
     * 3. 计算总价
     * 4. 扣库存（调用商品服务）
     * 5. 订单入库
     */
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@Valid OrderForm orderForm,
                                                BindingResult bindingResult){
        //当postman中数据没填写完成就会进入这里
        if(bindingResult.hasErrors()){
            log.error("【创建订单】参数不正确， orderForm={}", orderForm);
                                                //姓名必填
            throw new OrderException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        //  orderForm -> orderDTO
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("【创建订单】购物车不能为空");
            throw new OrderException(ResultEnum.CART_EMPTY);

        }

        OrderDTO createresult = orderService.create(orderDTO);
        Map<String, String> map =  new HashMap<>();
        map.put("orderId", createresult.getOrderId());

        return ResultVOUtil.succcess(map);
    }
}
