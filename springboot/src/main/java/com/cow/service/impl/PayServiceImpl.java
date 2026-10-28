package com.cow.service.impl;

import com.alipay.api.AlipayApiException;

import com.cow.domain.AlipayBean;
import com.cow.service.IPayService;
import com.cow.util.AlipayUtile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PayServiceImpl implements IPayService
{

    @Autowired
    private AlipayUtile alipayUtile;

    @Override
    public String aliPay(AlipayBean alipayBean) throws AlipayApiException {
        return alipayUtile.pay(alipayBean);
    }

}
