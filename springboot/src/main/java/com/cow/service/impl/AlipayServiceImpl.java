//package com.cow.service.impl;
//
//import com.alipay.api.AlipayApiException;
//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
//import com.alipay.api.domain.AlipayTradeRefundModel;
//import com.alipay.api.domain.AlipayTradeWapPayModel;
//import com.alipay.api.request.AlipayTradePagePayRequest;
//import com.alipay.api.request.AlipayTradeRefundRequest;
//import com.cow.dto.CodeMsg;
//import com.cow.dto.OrderDTO;
//import com.cow.dto.ResponseDTO;
//import com.cow.service.AlipayService;
//import com.cow.service.OrderService;
//import com.cow.util.AlipayUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.jasypt.commons.CommonUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//
//
//
//
///**
// *
// * @description 支付宝业务逻辑
// */
//@Slf4j
//@Service
//public class AlipayServiceImpl implements AlipayService {
//    private static final String PRODUCTCODE = "FAST_INSTANT_TRADE_PAY";
//    private static final String WAIT_SEND = "待发货";
//    @Autowired
//    private OrderService orderService;
//    @Resource
//    private AlipayUtil alipayUtil;
//    @Override
//    public String create(String orderId, String orderName, String payPrice) {
//        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID,
//                AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT,
//                AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,
//                AlipayConfig.SIGNTYPE);
//        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
//        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
//        model.setOutTradeNo(orderId);
//        model.setSubject(orderName);
//        model.setTotalAmount(payPrice);
//        model.setProductCode(PRODUCTCODE);
//        payRequest.setBizModel(model);
//        payRequest.setReturnUrl(AlipayConfig.RETURN_URL);
//        payRequest.setNotifyUrl(AlipayConfig.NOTIFY_URL);
//        try {
//            return client.pageExecute(payRequest).getBody();
//        } catch (AlipayApiException e) {
//            log.error("[支付宝] 支付失败", e);
//        }
//        return null;
//    }
//
//    @Override
//    public ResponseDTO<String> aliPayOrder(OrderDTO orderDTO) {
//
//        BigDecimal totalPrice = new BigDecimal("1.11");
//        String orderNo = "111111111";
//
//        return initAliPay(orderNo, totalPrice);
//    }
//
//    @Override
//    public ResponseDTO<String> initAliPay(String orderNo, BigDecimal totalPrice) {
//        return null;
//    }
//
//
//    /**
//     * 初始化支付信息
//     * @param orderNo
//     * @param totalPrice
//     * @return
//     */
//    public ResponseDTO<String> initAliPay(String orderNo, BigDecimal totalPrice) {
//        if(StringUtils.isNotBlank(orderNo)){
//            Order orderInfo = orderService.selectByOrderNo(orderNo);
//            Order order = new Order();
//            order.setOrderId(orderInfo.getOrderId());
//            order.setPayType(orderInfo.getPayType());
//            order.setUserAccount(orderInfo.getUserAccount());
//            order.setOrderState(WAIT_SEND);
//            totalPrice = BigDecimal.valueOf(orderInfo.getPayPrice());
//        }
//        // 封装沙箱支付宝支付信息
//        AlipayBean alipayBean = new AlipayBean();
//        alipayBean.setOut_trade_no(orderNo);
//        alipayBean.setSubject("乡村振兴农产品营销平台支付");
//        alipayBean.setTotal_amount(String.valueOf(totalPrice));
//        alipayBean.setBody("欢迎您在乡村振兴农产品营销平台上下单！！");
//        String pay = null;
//        try {
//            pay = alipayUtil.pay(alipayBean);
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }
//        return ResponseDTO.success(pay);
//    }
//
//    /**
//     * 沙箱支付宝成功支付回调接口
//     * @param orderDTO
//     * @return
//     */
//    @Override
//    public ResponseDTO<Boolean> aliPayOrderSuccess(OrderDTO orderDTO) 	{
//        // 这里可以写一些回调的接口逻辑，比如更新订单状态
//        if(CommonUtils.isEmpty(orderDTO.getNo())){
//            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
//        }
//        // 因为我们这有一个OrderDTO的入参，我们可以使用这个实体里面的订单号字段来获取详细订单信息
//        return ResponseDTO.successByMsg(true, "支付成功！");
//    }
//
//
//    @Override
//    public void refund(String orderId, String payPrice) throws AlipayApiException {
//        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID,
//                AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET,
//                AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
//        AlipayTradeRefundRequest payRequest = new AlipayTradeRefundRequest();
//        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
//        model.setOutTradeNo(orderId);
//        model.setRefundAmount(payPrice);
//        payRequest.setBizModel(model);
//        client.execute(payRequest).getBody();
//    }
//}
