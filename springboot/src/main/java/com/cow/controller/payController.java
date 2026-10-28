package com.cow.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;

import com.cow.entity.Order;
import com.cow.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.cow.util.AlipayUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/alipay")
public class payController {
    @Autowired
    private AlipayUtil payUtil;
    @Autowired
    private OrderService orderService;

    private Order orderModels = null;
    private String tokens = "";

    @ResponseBody
    @GetMapping("/pay")
    public String alipay(String token,  Order orderModel) throws AlipayApiException {
        orderModels = orderModel;
        tokens = token;
        //生成订单号（支付宝的要求？）
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String user = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        Order order = orderService.selectByOrderNo(orderModel.getOrderNo());
        String OrderNum = time + user;
        float oderValue = order.getPayPrice().floatValue();
        System.out.println("商品名字购买类型是" + orderModel.getPayType());
        //调用封装好的方法（给支付宝接口发送请求）
        return payUtil.sendRequestToAlipay(OrderNum, oderValue, orderModel.getPayType());

    }

    //    当我们支付完成之后跳转这个请求并携带参数，我们将里面的订单id接收到，通过订单id查询订单信息，信息包括支付是否成功等
    @GetMapping("/toSuccess")
    public String returns(String out_trade_no) throws ParseException {
        String query = payUtil.query(out_trade_no);
        System.out.println("==>" + query);
        JSONObject jsonObject = JSONObject.parseObject(query);
        Object o = jsonObject.get("alipay_trade_query_response");
        Map map = (Map) o;
        System.out.println(map);
        Object s = map.get("trade_status");
        if (s.equals("TRADE_SUCCESS")) {
            //当支付成功之后要执行的操作
            System.out.println("订单支付成功");

            orderModels.setOrderId(orderModels.getOrderId());
            boolean result = orderService.updateById(orderModels);


            System.out.println(s);
            return "redirect:http://localhost:8080/#/myorder";
        } else {
//            支付失败要执行的操作
            System.out.println("订单支付失败");
            return "index";
        }
    }

    /*
参数1：订单号
参数2：订单金额
参数3：订单名称
 */

}