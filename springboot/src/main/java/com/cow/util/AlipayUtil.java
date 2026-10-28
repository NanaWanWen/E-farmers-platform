package com.cow.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.cow.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlipayUtil {
    @Autowired
    private OrderService orderService;
    //appid
    private final String APP_ID = "2021000146627253";
    //应用私钥
    private final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCA/pAnYcUr8AaABhNdw60sEAwnR6IafQHKwJwjqH64wwLPnO0zzhdk3Pb238TUdVMdUEArDeUwziE5wrTxuMeDwp3lQsO6UOYVDLDQ+nuTb92a6OhMQ57dWOPCRDHlTwIK1uQtvcDDhu/QkvGI18tgcprP2+Iaq0H4ILX46uz/2NF62Xr6gTsN97ZDEUCoai5N4nl7gZfhMy1DZ3rnp1cx0neUdx3Q59SO04+s7IJSn0Rr3SBt/jvavUCYKld+kzQDReBAPIwCktX6TzL+nBhGRcbh1/meXW559xG1b+SSAb86fDlS0L6+BNRkxGU6onDBqKotlijiV4bMPY41RhK5AgMBAAECggEAKkpLHH4zVFpW5zYWt1Dlv2JreZ1Eigadxckr54zgLtbQXxyFr8xLYygGV525PCZS33Nl9jeSAlQyFGu/QthfoNT3Syh+XuES7afBeGIGjanJL3ZYYlhxBwSTdydAbHtgogkdudlSD0h36xuh0ElfRfmMw8TE9hVbhWvD/R2YIbCOnEDQp9K6U8b/VpqNC36cECjpkLcUW2J4sLsxBuvZdqddxAenWtSlApnO7Y2ob3hZUCubO6DSznx1g09ZAgGizh3hCHbxQp8fkvEXod/lqZINXwZxRVMBml3CB3DzKHJaC63EOLa20tM9SyZMwOsmf7vLNMJEZZ6o21G83REaYQKBgQC6O+PqS4pJ1vbdx07Wr3DXhqmlAthA8GHz70AgWe9d5rrl3ekmgZCDq+2xHumH5A18FsI/K409kXTQnO6qrTbB6NGBAObkc9BQIZwXI47SsZc13khnM/gIkDH7v5N107t+0sNS3uri1cCoBiwvtGm4TRG/u0QFH2PsxCaE3j5rTQKBgQCxUVCVOi8I94F81w0r0yMLuuP5+71eD2eKEo13REojod5+G9AZpHW/gv+WQ4ODuVArsfUeCKd8cpyss52D3NbZGOXDR4g/bZWsZtu7MRsM/fzb346FGQPgecFQ5nSf0Llp3jp8bgA4TLTu1lGUZhOmGKDLOS8e1IZu5ofwNtAXHQKBgFW9zyAGqWN3me7fnTeQTHaeJCq5lZDIW4CwbSi331GvMes08OvuiPdmR6fdYI1U/LRd0g69YWiwOET3i5Z+6w/5vcMUBDm37HeIEdJBZ7cvVjvR3CnPrmla4fhS6nQ8CD3KkWzs/t1ypiF1vhA0ktvsd7h7YkStgBb48/cGT1ydAoGBAKlYzsxmsEsxDwMq5BJp6LuoN1klmzr9HVbZ1zAEEYasbrEkYQBMeIR6WR2qxy0DzFSe9kpD26nYUFgEhzhLrwnSZIW3iKGDSIJKDitef8ZNwLRBc30cYf5+aimYn+OOVbaNnm/+IsmiQN8l4u4G+1cTiGXu7FjWgrniBAxgXSblAoGAJlSLwTQx4bHrb1BZJKRvRlOjrBx/6SWqPsBQQpTpQExlmKOQurdb24Wr2E+hvfGz08vil8+WhHCzRUsxHbMq4yPkxNa0/EfNjGElhYrMgEcl95nE9Kt+cQKk7UCWRUe0QRJ3W8m7cqj8Wn7M5ymYj7BcdZ/XR2LKKzLTodia1P4=";

    private final String CHARSET = "UTF-8";
    // 支付宝公钥
    private final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhtyNDL3kMQSieV8fxIPgYicgGWnXZOxYDm0ouL7xds1Sk2W1Bsbp7oPESwvg9DiLRLjDQWTxqf7HeaWJfA4cWecEUP+mH3eioHcSh7M8kXv3Q3A+JeqRowC8xIU9Ye2IAVc0bgNL6o1A2bYxSeqVHcaI/plW7mchsLfx3Lrm0mhkZFiDpgJGJEbW7hcoEMkKsOvS0AKn49VajAh/9r+i1Avh8vtkUpBEdgtujeLh9z6OUxDW51LJufwbzSUjUTfIB3eZk/0UHOhpiP77judBdT5Txd+5rBYb9yvSSym69uwmSSOznmaABXNHLj01T4IN/1FSJNUBsIMiMjkkcXmdmQIDAQAB";
    //这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    private final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private final String FORMAT = "JSON";
    //签名方式
    private final String SIGN_TYPE = "RSA2";
    //支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
        private final String NOTIFY_URL = "http://dji2rq.natappfree.cc/api/alipay/toSuccess";
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private final String RETURN_URL = "http://localhost:8080/api/alipay/toSuccess";
    private AlipayClient alipayClient = null;
    //支付宝官方提供的接口
    public String sendRequestToAlipay(String outTradeNo, Float totalAmount, String subject) throws AlipayApiException {
        //获得初始化的AlipayClient
        alipayClient = new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(RETURN_URL);
        alipayRequest.setNotifyUrl(NOTIFY_URL);

        //商品描述（可空）
        String body = "";
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        System.out.println("返回的结果是："+result );
        return result;
    }

    //    通过订单编号查询
    public String query(String id){
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", id);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        String body=null;
        try {
            response = alipayClient.execute(request);
            body = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return body;
    }
}
