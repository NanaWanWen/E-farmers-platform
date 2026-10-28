package com.cow.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MultiOrderPaymentRequest {
    private List<String> orderNos ;
    private String payType;
    private String cardIds;
    private String totalPayPrice;
//    private List<String> orderNos;
}
