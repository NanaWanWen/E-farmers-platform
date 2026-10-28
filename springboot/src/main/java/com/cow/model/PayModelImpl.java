package com.cow.model;

import com.alipay.api.AlipayApiException;

import com.cow.domain.AlipayBean;
import com.cow.service.IPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class PayModelImpl implements IPayModel
{
    @Autowired
    private IPayService service;


    @Override
    public String aliPay(AlipayBean alipayBean) throws AlipayApiException
    {
        return service.aliPay(alipayBean);
    }
}
