# 乡村振兴电商平台（全栈商城 / 供应链协同）

基于 **SpringBoot + Vue** 的全栈电商与供应链管理系统，面向乡村特产零食的线上销售与采购协同场景，涵盖商品、订单、支付、采购、退货、会员营销、运营数据看板与 RBAC 权限等完整业务链路。

## ✨ 功能特性

- **商品中心**：商品分类、品牌、规格、商品评价、轮播图（Banner）管理
- **交易与库存**：下单实时校验并扣减库存，库存不足拦截，扣减后联动缺货标记与商品下架
- **支付**：支付宝沙箱下单与异步回调 RSA 验签；多订单合并支付（Redis 缓存子单号）
- **采购供应链**：供应商管理、采购单、收货入库回增库存，打通"采购—销售"链路
- **售后**：退货申请与处理，退货原因可配置，售后全程可追溯
- **会员与营销**：VIP 会员、轮播营销位
- **运营数据看板**：ECharts 聚合商品/订单/退货多维指标，按时间范围筛选销售趋势与品类占比
- **认证鉴权**：基于 Sa-Token 的登录鉴权、密码加盐、账号封禁；RBAC 角色模型，后端全局异常统一收敛越权/封禁，前端路由按角色鉴权
- **工程化**：统一响应体规范、模板邮件异步发送、敏感词 DFA 过滤、Druid 连接池

## 🛠 技术栈

| 层 | 技术 |
|---|---|
| 后端 | SpringBoot 2.3.7、MyBatis-Plus、MySQL、Redis、Sa-Token、Druid、支付宝支付 |
| 前端 | Vue 2、Element UI、Vuex、Vue Router、ECharts（v-charts） |
| 工具 | 模板邮件（Spring Mail）、敏感词过滤、统一响应体 |

## 📁 目录结构

```text
乡村振兴平台/
├── springboot/          # 后端服务（SpringBoot + MyBatis-Plus）
│   └── src/main/java/com/cow/...
├── vue/                 # 前端管理后台（Vue2 + Element UI）
│   └── src/components/  # mall / product / order / purchase / return / user / marketing ...
└── assets/              # 项目截图
```

## 🚀 环境准备

- JDK 1.8+
- MySQL 5.7+
- Redis
- Node.js（前端构建）

## ⚙️ 运行

**后端**

```bash
cd springboot
# 修改 src/main/resources/application-*.yml 中的数据库、Redis、支付宝等配置
mvn spring-boot:run
```

**前端**

```bash
cd vue
npm install
npm run serve
```

## 📸 系统截图

> 将截图按以下命名放入 `assets/` 目录即可正常显示。

| 页面 | 截图 |
|---|---|
| 登录 / 注册 | ![登录](assets/login.png) |
| 运营数据看板 | ![看板](assets/dashboard.png) |
| 商品管理 | ![商品](assets/product.png) |
| 订单列表 | ![订单](assets/order.png) |
| 采购 / 供应商 | ![采购](assets/purchase.png) |
| 退货处理 | ![退货](assets/return.png) |
| VIP / 营销 | ![营销](assets/vip.png) |

## 📌 说明

- 支付使用支付宝沙箱环境，回调地址需在 `PayConfig` / `application-*.yml` 中配置。
- 验证码通过邮件发送，需配置 `application-mail.yml` 中的 SMTP 信息。
- 暂无建表 SQL，表结构由实体类与 `springboot/src/main/resources/mapper` 对应，按需初始化。
