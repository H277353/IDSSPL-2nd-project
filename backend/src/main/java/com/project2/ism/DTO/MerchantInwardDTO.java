package com.project2.ism.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MerchantInwardDTO {
    private Long outwardId;
    private String productName;      // from Product
    private Long quantity;
    private String deliveryMethod;
    private String trackingNumber;
    private LocalDate dispatchDate;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime receivedDate; // null → still in transit

    // getters + setters

    public Long getOutwardId() {
        return outwardId;
    }

    public void setOutwardId(Long outwardId) {
        this.outwardId = outwardId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDate getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(LocalDate dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }
}
