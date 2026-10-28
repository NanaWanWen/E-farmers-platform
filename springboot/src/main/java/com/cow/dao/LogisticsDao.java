package com.cow.dao;

import com.cow.entity.Logistics;
import org.springframework.stereotype.Repository;

/**
 *
 * @email QQ550080747
 * @date 2020/11/26 17:44
 * @description 物流表
 */
@Repository
public interface LogisticsDao extends BaseDao<Logistics> {
    /**
     * 根据订单编号查询
     * @param orderNo 订单编号
     * @return 物流信息
     */
    Logistics selectOrderNo(String orderNo);
}
