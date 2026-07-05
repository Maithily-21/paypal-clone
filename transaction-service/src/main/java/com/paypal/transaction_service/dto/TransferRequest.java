package com.paypal.transaction_service.dto;

public class TransferRequest {
    private String senderName;
    private String recieverName;
    private Double amount;

    public TransferRequest() {
    }
    public TransferRequest(String senderName, String recieverName, Double amount) {
        this.senderName = senderName;
        this.recieverName = recieverName;
        this.amount = amount;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
