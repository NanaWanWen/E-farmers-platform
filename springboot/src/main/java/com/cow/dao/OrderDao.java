package com.cow.dao;

import com.cow.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @email QQ550080747
 * @date 2020/11/18 10:20
 * @description 订单表
 */
@Repository
public interface OrderDao extends BaseDao<Order> {
    /**
     * 根据用户账号查询订单信息
     *
     * @param userAccount 用户账号
     * @return 订单信息
     */
    List<Map<String, Object>> selectAllOrder(String userAccount);

    /**
     * 查询订单基本信息
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    Map<String, Object> selectOrder(String orderNo);

    /**
     * 查询今日订单数量、今日销售总额、昨日销售总额等信息
     *
     * @return 统计数据
     */
    Map<String, Object> orderOverview();

    /**
     * 获取订单图表分析数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    List<Map<String, Object>> selectChartDate(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取订单商品类别图表分析数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    List<Map<String, Object>> selectProductTypeChart(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取某个时间段，订单总数和销售总额
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    Map<String, Object> selectCountAndAmount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据订单编号批量查询订单信息
     *
     * @param orderNos 订单编号
     * @return 订单信息
     */
    List<Order> listByOrderNos(List<String> orderNos);

    /**
     * 根据订单号查询订单信息
     *
     * @return 订单信息
     */
    @Select("select * from `order` where trade_no=#{tradeNo} ")
    List<Order> selectBytradeNo(String tradeNo);

    @Select("select * from  `order` where  order_from=#{orderTime}")
    List<Order> selectByOrderTime(Date orderTime);
}
