package com.cow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.cow.config.AlipayConfig;
import com.cow.domain.AlipayBean;
import com.cow.dto.MultiOrderPaymentRequest;
import com.cow.entity.Logistics;
import com.cow.entity.Order;
import com.cow.entity.Product;
import com.cow.model.IPayModel;
import com.cow.service.LogisticsService;
import com.cow.service.OrderService;
import com.cow.service.ProductService;
import com.cow.service.ShoppingCartService;
import com.cow.util.ThreadLocalUtil;
import com.cow.util.general.CommonResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @email QQ550080747
 * @description 订单相关业务
 */

@RestController
@CrossOrigin
public class OrderController {
    private static final String VIP = "Vip";
    private static final String COLLECT_GOODS_STATE = "已收货";
    private static final String WAIT_SEND = "待发货";

    @Autowired
    private OrderService orderService;
    @Autowired
    private IPayModel model;
    @Autowired
    private AlipayConfig config;

    @Autowired
    private ProductService productService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @RequestMapping(value = "/order/findById")
    public CommonResult findOrderById(Integer orderId) {
        Order order = orderService.selectById(orderId);
        if (orderId != null) {
            return CommonResult.success("订单信息查询成功", order);
        }
        return CommonResult.error("订单信息查询失败");
    }

    @RequestMapping(value = "/order/findOrderInfo")
    public CommonResult findOrderInfo(String userAccount) {
        List<Map<String, Object>> orderMap = orderService.selectAllOrder(userAccount);
        if (orderMap != null) {
            return CommonResult.success("订单信息查询成功", orderMap);
        }
        return CommonResult.error("订单信息查询失败");
    }

    @RequestMapping(value = "/order/findAll")
    public CommonResult findAllOrder() {
        List<Order> orders = orderService.selectAll();
        if (orders != null) {
            return CommonResult.success("订单信息查询成功", orders);
        }
        return CommonResult.error("订单信息查询失败");
    }

    @RequestMapping(value = "/order/findCount")
    public CommonResult findCount() {
        Integer count = orderService.selectCount();
        if (count != null) {
            return CommonResult.success("订单数量查询成功", count);
        }
        return CommonResult.error("订单数量查询失败");
    }


    @RequestMapping(value = "/order/add")
    public CommonResult addOrder(Order order) {
        if (order != null) {
            if (order.getProductNo().contains(VIP)) {
                return handleMemberOrders(order);
            }
            return handleMerchandiseOrders(order);
        } else {
            return CommonResult.error("订单数据不完整");
        }
    }

    @PostMapping("/alipay/notify")
    public String handleAlipayNotify(HttpServletRequest request) {
        try {
            // 解析请求参数
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = String.join(",", values);
                params.put(name, valueStr);
            }

            // 验签
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    config.getPublicKey(),
                    config.getCharset(),
                    config.getSignType()
            );
            if (!signVerified) {
                return "fail"; // 验签失败
            }

            // 获取支付相关信息
            String outTradeNo = params.get("out_trade_no"); // 支付单号
            String tradeNo = params.get("trade_no");        // 支付宝交易号
            String tradeStatus = params.get("trade_status"); // 支付状态

            // 区分支付类型：合并支付或单订单支付
            List<String> orderNos;
            String json = redisTemplate.opsForValue().get("combined:" + outTradeNo);
//            String json = redisTemplate.opsForValue().get(outTradeNo);
                // 合并支付：从 Redis 获取子订单号列表
                if (json != null) {{
                    orderNos = JSON.parseArray(json, String.class);

//                    return "fail"; // 主单号无效
                }
            } else {
                // 单订单支付：直接使用支付单号作为订单编号
                orderNos = Collections.singletonList(outTradeNo);
            }
            // 处理支付成功的状态
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                for (String orderNo : orderNos) {
                    Order order = orderService.selectByOrderNo(orderNo);
                    if (order != null && !order.getOrderState().equals(WAIT_SEND)) {
                        // 更新订单状态
                        order.setOrderState(WAIT_SEND); // 设置为待发货状态
                        order.setPayType("支付宝");     // 设置支付方式
                        order.setTradeNo(tradeNo);      // 设置支付宝交易号
//                  order.setPayTime(LocalDateTime.now()); // 设置支付时间
                        orderService.updateById(order);
                        // 移除已处理的 orderNo
                        String combinedKey = "combined:" + outTradeNo;
                        redisTemplate.delete(combinedKey);
//                        redisTemplate.opsForSet().remove("orderNos", outTradeNo);
                        // 获取并移除 cardIds（假设所有订单共享相同的 cardIds）
//                        if (redisTemplate.opsForSet().size("orderNos") == 0) {
                            String cardIds = (String) redisTemplate.opsForValue().get("cardIds");
                            redisTemplate.delete("cardIds");
                            // 进一步处理 cardIds，比如删除购物车数据
                            ObjectMapper mapper = new ObjectMapper();
                        if (cardIds != null&& !cardIds.isEmpty()){
                            Integer[] cardIdList = mapper.readValue(cardIds, Integer[].class);
                            for (Integer cardId : cardIdList) {
                                shoppingCartService.deleteById(cardId);
                            }
                        }
                    }
                }
            }

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    /**
     * 单订单支付接口
     *
     * @param orderNo   订单编号
     * @param payType   支付类型
     * @param cardIds   卡券 ID（可选）
     * @param payPrice  支付金额（可选，用于校验）
     * @return 支付结果
     */
    @RequestMapping(value = "/order/pay", method = RequestMethod.POST)
    public CommonResult pay(
            @RequestParam String orderNo,
            @RequestParam(required = false) String payType,
            @RequestParam(required = false) String cardIds,
            @RequestParam(required = false) String payPrice) {

        // 根据订单编号查询订单信息
        Order order = orderService.selectByOrderNo(orderNo);
        if (order == null) {
            return CommonResult.error("订单不存在");
        }

        // 校验支付金额（如果传入了 payPrice 参数）
        if (payPrice != null) {
            BigDecimal requestPayPrice = new BigDecimal(payPrice);
            if (!requestPayPrice.equals(order.getPayPrice())) {
                return CommonResult.error("支付金额不匹配");
            }
        }

        // 构造支付请求内容
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", order.getOrderNo()); // 使用订单编号作为支付单号
        bizContent.put("total_amount", order.getPayPrice().toString());
        bizContent.put("subject", "商品支付：" + order.getProductNo());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        try {
            // 初始化 AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(
                    config.gatewayUrl,
                    config.appId,
                    config.privateKey,
                    "json",
                    config.charset,
                    config.publicKey,
                    config.signType
            );

            // 创建支付请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setBizContent(bizContent.toJSONString());
            request.setNotifyUrl(config.getNotifyUrl()); // 设置异步通知地址
            request.setReturnUrl(config.getReturnUrl()); // 设置同步返回地址

            // 执行支付请求，获取支付表单
            String form = alipayClient.pageExecute(request).getBody();
            return CommonResult.success("支付请求成功", form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return CommonResult.error("支付请求失败：" + e.getMessage());
        }
    }

    // 购物车订单支付
    @RequestMapping(value = "/order/car/pay", method = RequestMethod.POST)
    public CommonResult pay(@RequestBody MultiOrderPaymentRequest paymentRequest) {
        List<String> orderNos = paymentRequest.getOrderNos();
        List<Order> orders = orderService.listByOrderNos(orderNos);
        String cardIds = paymentRequest.getCardIds();
        // 合单主订单号
        String combinedOrderNo = UUID.randomUUID().toString().replaceAll("-", "");
        // 把 orderNos 存起来（Redis）
        redisTemplate.opsForValue().set(
                "combined:" + combinedOrderNo,
                JSON.toJSONString(orderNos), // 用 FastJSON 序列化 List<String>
                30, TimeUnit.MINUTES
        );

        // 只存储一次 cardIds
        if (Boolean.FALSE.equals(redisTemplate.hasKey("cardIds"))) {
            redisTemplate.opsForValue().set("cardIds", cardIds);
        }

        BigDecimal totalAmount = orders.stream()
                .map(Order::getPayPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", combinedOrderNo); // 主单号
        bizContent.put("total_amount", totalAmount.toString());
        bizContent.put("subject", "合并订单支付");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setBizContent(bizContent.toJSONString());
            request.setNotifyUrl(config.getNotifyUrl());
            request.setReturnUrl(config.getReturnUrl());
            // 初始化 AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(config.gatewayUrl, config.appId,
                    config.privateKey, "json", config.charset, config.publicKey,config.signType);
            String form = alipayClient.pageExecute(request).getBody();
            return CommonResult.success("支付请求成功：",form);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error("支付请求失败：" + e.getMessage());
        }
    }


    @RequestMapping(value = "/alipay/refund")
    public CommonResult refund(String orderNo, String payPrice ) {
        System.out.println("orderNo"+orderNo);
        String outRequestNo = "REFUND_" + UUID.randomUUID().toString().replace("-", "");
        // 根据 orderNo 获取对应的 trade_no
        Order order = orderService.selectByOrderNo(orderNo);
        System.out.println("order"+order);
        if (order == null || order.getTradeNo() == null) {
            return CommonResult.error("未找到有效的订单或 trade_no");
        }
        String tradeNo = order.getTradeNo();
        List<Order> orders =orderService.selectBytradeNo(tradeNo);
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        for (Order o : orders) {
            totalAmount=  totalAmount.add(o.getPayPrice());
        }
        // 初始化 AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(config.gatewayUrl, config.appId,
                config.privateKey, "json", config.charset, config.publicKey, config.signType);

        AlipayTradeRefundRequest req = new AlipayTradeRefundRequest();
        req.setBizContent("{\"trade_no\":\"" + tradeNo + "\"," +
                "\"refund_amount\":\"" + totalAmount + "\"," +
                "\"out_request_no\":\"" + outRequestNo + "\"}");
        AlipayTradeRefundResponse resp = null;
        try {
            resp = alipayClient.execute(req);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return CommonResult.error("退款请求失败: " + e.getMessage());
        }
        if (resp.isSuccess()) {
            return CommonResult.success("退款成功", totalAmount);
//            return CommonResult.success("退款成功", resp.getBody());
        } else {

            return CommonResult.error("退款失败: " + resp.getSubMsg());
        }
    }


    @RequestMapping(value = "/order/cartOrder")
    public CommonResult cartOrder(String orderNo, String ordersInfo, String cartIds) {
        List<String> cartIdList = JSON.parseArray(cartIds, String.class);
        List<Order> orders = JSON.parseArray(ordersInfo, Order.class);
        if (orders != null) {
            ArrayList<String> orderInfo = new ArrayList<>();
            ArrayList<String> productInfo = new ArrayList<>();
            for (Order order : orders) {
                Product product = productService.selectByKey(order.getProductNo());
                Integer productStock = product.getProductStock();
                Integer payAmount = order.getPayAmount();
                if (productStock >= payAmount) {
                    Product newProduct = new Product();
                    newProduct.setProductId(product.getProductId());
                    int newStock = productStock - payAmount;
                    newProduct.setProductStock(newStock);
                    newProduct.setIsStockOut(newStock < product.getLowestStock());
                    // 如果库存小于等于0，自动下架
                    newProduct.setIsSale(newStock > 0);
                    if (productService.updateById(newProduct) && orderService.insertData(order)) {
                        orderInfo.add(order.getOrderNo());
                        productInfo.add(order.getProductNo());
                    }
                }
            }
            if (!orderInfo.isEmpty()) {
                String cartIdsInfo = StringUtils.join(cartIdList.toArray(), ",");
                String orderNoInfo = StringUtils.join(orderInfo, ",");
                String productNoInfo = StringUtils.join(productInfo, ",");
                redisTemplate.opsForValue().set(orderNo, orderNoInfo, 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set("cartId" + orderNo, cartIdsInfo, 24, TimeUnit.HOURS);
                return CommonResult.success("创建订单成功", orderNoInfo);
            }
            return CommonResult.error("创建订单失败，请查看商品库存是否满足购买数量");
        } else {
            return CommonResult.error("订单数据不完整");
        }
    }

    @RequestMapping(value = "/order/update")
    public CommonResult updateOrder(Order order) {
        if (orderService.updateById(order)) {
            return CommonResult.success("修改订单成功", order);
        }
        return CommonResult.error("修改订单失败");
    }

    @RequestMapping(value = "/order/delete")
    public CommonResult deleteOrder(Integer orderId) {
        if (orderService.deleteById(orderId)) {
            return CommonResult.success("删除订单成功", "订单id：" + orderId);
        }
        return CommonResult.error("删除订单失败");
    }

    @RequestMapping(value = "/order/receipt")
    public CommonResult updateOrder(Integer orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderState(COLLECT_GOODS_STATE);
        if (orderService.updateById(order)) {
            return CommonResult.success("商品收货成功", order);
        }
        return CommonResult.error("商品收货失败");
    }

    @RequestMapping(value = "/orderDetail/orderInfo")
    public CommonResult orderInfo(String orderNo) {
        ArrayList<Object> resultList = new ArrayList<>();
        Order order = orderService.selectByKey(orderNo);
        Logistics logistics = logisticsService.selectOrderNo(orderNo);
        if (order != null) {
            resultList.add(order);
        }
        if (logistics != null) {
            resultList.add(logistics);
        }
        return CommonResult.success("订单详情查询成功", resultList);
    }

    /**
     * 处理会员订单
     *
     * @param order 订单信息
     */
    private CommonResult handleMemberOrders(Order order) {
        if (orderService.insertData(order)) {
            return CommonResult.success("创建订单成功", order);
        } else {
            return CommonResult.error("创建订单失败");
        }
    }

    /**
     * 处理商品订单
     *
     * @param order 订单信息
     */
    private CommonResult handleMerchandiseOrders(Order order) {
        Product product = productService.selectByKey(order.getProductNo());
        Integer productStock = product.getProductStock();
        Integer payAmount = order.getPayAmount();
        boolean isOk = productStock >= payAmount;
        if (isOk) {
            Product newProduct = new Product();
            newProduct.setProductId(product.getProductId());
            int newStock = productStock - payAmount;
            newProduct.setProductStock(newStock);
            newProduct.setIsStockOut(newStock < product.getLowestStock());
            // 如果库存小于等于0，自动下架
            newProduct.setIsSale(newStock > 0);
            // 更新商品库存
            if (productService.updateById(newProduct)) {
                // 插入订单信息
                // order.setOrderState("待发货");
                if (orderService.insertData(order)) {
                    // 缓存订单号
                    redisTemplate.opsForValue().set(order.getOrderNo(), order.getOrderNo(), 24, TimeUnit.HOURS);
                    return CommonResult.success("创建订单成功", order);
                } else {
                    return CommonResult.error("创建订单失败");
                }
            } else {
                return CommonResult.error("创建订单失败");
            }
        } else {
            return CommonResult.error("商品库存不足");
        }
    }
}
