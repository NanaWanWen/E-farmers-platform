package com.cow.service;


import com.alipay.api.AlipayApiException;
import com.cow.dto.OrderDTO;
import com.cow.dto.ResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 *
 */
public interface AlipayService {
    /**
     * 创建支付订单
     *
     * @param orderId   订单编号
     * @param orderName 订单名称
     * @param payPrice  支付金额
     * @return 支付表单
     */
    String create(String orderId, String orderName, String payPrice);
    // 使用沙箱支付宝支付订单
    ResponseDTO<String> aliPayOrder(OrderDTO orderDTO);

    ResponseDTO<String> initAliPay(String orderNo, BigDecimal totalPrice);
    // 沙箱支付宝成功支付回调接口
    ResponseDTO<Boolean> aliPayOrderSuccess(OrderDTO orderDTO);
    /**@
     * 订单退款
     *
     * @param orderId  订单编号
     * @param payPrice 退款金额
     * @throws AlipayApiException 支付异常
     */
    void refund(String orderId, String payPrice) throws AlipayApiException;
}
