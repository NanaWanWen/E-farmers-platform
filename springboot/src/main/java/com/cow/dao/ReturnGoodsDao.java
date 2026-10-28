package com.cow.dao;


import com.cow.entity.ReturnGoods;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Repository
public interface ReturnGoodsDao extends BaseDao<ReturnGoods> {
    /**
     * 查询用户退货商品信息
     *
     * @param userNumber 用户账号
     * @return 退货信息
     */
    List<ReturnGoods> selectAllByUser(String userNumber);

    /**
     * 查询全部订单基本信息
     *
     * @param userAccount 用户账号
     * @return 退货信息
     */
    List<Map<String, Object>> selectAllOrder(String userAccount);

    /**
     * 查询未退款的订单
     *
     * @return 退货信息
     */
    Map<String, Object> returnGoodsOverview();

    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return 退货信息
     */
    Map<String, Object> selectOrderNo(String orderNo);

    @Select("select * from  return_goods where apply_time =#{applyTime}")
    List<ReturnGoods> selectByApplyTime(String applyTime);

   @Select(" SELECT rg.* FROM return_goods rg INNER JOIN `order` o ON rg.order_no = o.order_no WHERE o.trade_no = ( SELECT trade_no FROM `order` WHERE order_no = #{orderNo}) ")
    List<ReturnGoods> findReturnGoodsByOrderNo(String goodsOrderNo);
}
