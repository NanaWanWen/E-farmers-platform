package com.cow.service;

import com.alipay.api.AlipayApiException;
import com.cow.domain.AlipayBean;


public interface IPayService
{
    /**
     * 支付宝支付接口
     * @param alipayBean
     * @return
     * @throws AlipayApiException
     */
    String aliPay(AlipayBean alipayBean) throws AlipayApiException;

}
