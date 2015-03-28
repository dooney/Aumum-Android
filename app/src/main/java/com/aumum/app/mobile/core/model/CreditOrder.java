package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 28/03/2015.
 */
public class CreditOrder extends AggregateRoot {

    private String userId;
    private String creditGiftId;
    private String deliveryDetails;
    private int orderTotal;

    public CreditOrder(String userId,
                       String creditGiftId,
                       String deliveryDetails,
                       int orderTotal) {
        this.userId = userId;
        this.creditGiftId = creditGiftId;
        this.deliveryDetails = deliveryDetails;
        this.orderTotal = orderTotal;
    }
}
