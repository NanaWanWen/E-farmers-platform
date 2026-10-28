package com.cow.config;



import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件
 */
@Configuration
@Data
@Component
public class AlipayConfig {

    /**
     * 应用ID,您的APPID，
     */
    public String appId ="2021000148623222";

    /**
     * 商户私钥
     */
    public String privateKey ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgsueVNTij9VjoYcYnmzAbhPpvGNFuyED4EIdGjDpfdglAlnzTrXarPT8WMwpDf0OtHm6WEefkv1OyZ3PjSCdfgVIsPJhx3pQm8F9OqueKeesz/axViemLuzlM/wZfbzBhAHRSRuvTtofAGRv7NO8ojtER3KcXL1PoCpEgLRCPwS5AZWSTcgxIUBlVQ1KV19XJWo30ym5/Ae/MThhchKjKFla/CyVpPbHG7qDRjLj5WLSURW6gxX7pa9muTD/7wPtcO5bARWLIGpnTQzF5jd8tg2rjlLVcz5deeOVG0U5ciU6HYj2Q9jM5NRdisy6vAzVvWdSwlZLLSZey5oL50NnZAgMBAAECggEANV71twJ0C1BC9Pr0GtdZxOoHWrp9ZDOyEDE9HFsaT/Yd4IuabZsg3IJ831yYPg6YZXjD9aeAwuIgZBIj1QpzuS50IikF4FuyDLSskq4ENGOFkEDh6Y7iN/w4bbANZ8i/4peKCoFvdWMb9trgBjBwdaHpIeuMuxCG53wK8wrEg1l3YwtJsElzWBIRMxz0k79254rkyFDWQOLjwdhPvVjowPXw89jf+ZDJqZycG7zx0EBsxBw6oRilSpAb3rp2nJBNcUQDxYuRcNYeaylJzJpEhPK/2yJxmnafXQeMRJDNEpxgqMFvRfCyt4giw51UHTDYJOZYnKxIuQbGTlBmb3LbWQKBgQDqgRXjXV+F3CxLI/0UYmKAE1v2YrBpR0iNhdT9OfXdEDyuMDooVJ67ILriGY/Q9EUCQlOYHZavPsK4YGFdhO+kpTT4KEdsep6fTlrBlx4H6TMRYtiE9Smqevj3ZTGn8wixx6U3HLQ61DTr2JkixpWpsR5ao7yuhvyMqk1DeU1SAwKBgQCvbePI3fcyHzQLerYNE62qH3M9Ck7qr+OrqH4G9NJUNSAuYd23cpADEIvLar/wr4t2nNR4/kNpqDKj2MT5Mt9ty8eK1vJ4qXbP834gKqbG+YUCC8aN08fG4TUQY10PlwybFsut/eQDEjyq5UCCexsJB3oZvD9FQorqjyQ2coyr8wKBgQChQUbV5D1thSuIOCUtwUHOpIFWeiKpInWHrAH8Z8Dj1OiFN3Cmjxnfv/ACzmUQ4QgbN5TYQ3B87YFk3BHYBzPrWheEld4XgHJXRcAu40WpVvjg+cG+HAAc1RciuBlfTkYqSgkYTsq4sXf6viT/vuLnn34qAfIbaYN9Dxa2UVGUFQKBgCVOeD9yPKxzCE1cpjR3t9nS4F3dwFSWXRNXPJ3cGJKBzbsvYsgtW8TbEu+KXxEfM8hgp6qnX8yrUUGYyqK4F92wlVnIRj40w6iLfon2qArua0O5H1tv6y83G2LFzrePBsZAfcYdlQy2JyKsqCBXb7Nm3Kd2afofoY0qLahKifHTAoGAcqDrvg0vcFk1wHQzyE/1HmJylNdmywkUzj44g1wJIHjpyAJ5d2J4OdX2NZgKi+MwPnJT8YKeSQcHslLOVXHkcBEcR/facM5tNzaHwoe46+kQPv019O5SFKK7X9k7i9+q0maKCGgDmbXG1ZtTmqMChJDyTsgzyBkxtyviG5x6B8M=";

    /**
     * 支付宝公钥,
     */
    public String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0K+HPmeSDQdLpJPz34RhaZuC2Ih52qZbcBOxcns1OsgvBi3q+AX7tzuOwuslHSUEs9bOpAx28vZUfdbxGosKkepGuWu5kL8+HKRatpoHVdtuB7U2iSSvBH8CDSxMMxEb7NKm35GNsyzGvJQblw/1qX8RPyusXo9+BAjAWbQPmtUmFG1z++N3ZpHt7PT7kX68v/LCPWTacI/tBCUgJwLaSS7qCD1cLNAiGxEXyVhSXo9vPYOw6ZIpmU2///NLQD4iSImLEEH1BjY7EFy/p68RK90yfrq67Jgb4OTclS+19LXpIAKzWahB9BXhmnG8F7tt/xxw8HAauMr2uRHUFrkzwIDAQAB";


    public String notifyUrl="https://profound-troll-completely.ngrok-free.app/alipay/notify";


    public String returnUrl="http://localhost:8080/#/myOrder";

    // 签名方式
    public  String signType = "RSA2";

    // 字符编码格式
    public  String charset = "utf-8";

    // 支付宝网关
    public  String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";


    private String logPath = "D:\\";


}
