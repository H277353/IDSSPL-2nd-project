package com.project2.ism.DTO.ReportDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class FranchiseTransactionReportDTO {
    private String txnId;
    private Long customTxnId;
    private String actionOnBalance;
    private LocalDateTime txnDate;
    private BigDecimal txnAmount;
    private LocalDateTime settleDate;
    private String authCode;
    private String tid;

    private BigDecimal settlementRate; // merchant % of txnAmount
    private BigDecimal settleAmount;         // raw merchant amount
    private BigDecimal systemFee;            // raw system fee
    private BigDecimal franchiseRate;        // calculated from txnAmount & franchiseCommission
    private BigDecimal merchantRate;         // calculated from txnAmount & settleAmount
    private BigDecimal commissionRate;       // merchantRate - franchiseRate
    private BigDecimal commissionAmount;     // raw franchise commission
    private BigDecimal systemFeeExGST;
    private BigDecimal gstAmount;
    private BigDecimal tdsAmount;
    private BigDecimal tdsPercentage;
    private BigDecimal netCommissionAmount;
    private String brandType;
    private String cardType;
    private String cardClassification;
    private String merchantName;
    private String franchiseName;
    private String state;
    private String service;

    public FranchiseTransactionReportDTO(
            String txnId,
            Long customTxnId,
            String actionOnBalance,
            LocalDateTime txnDate,
            BigDecimal txnAmount,
            LocalDateTime settleDate,
            String authCode,
            String tid,
            BigDecimal merchantNetAmount,   // Can be NULL for standalone transactions
            BigDecimal grossCharge,         // Can be NULL
            BigDecimal franchiseCommission, // ftd.netAmount (franchise's own netAmount)
            BigDecimal systemFee,           // Can be NULL
            BigDecimal gstRate,
            BigDecimal tdsRate,
            String brandType,
            String cardType,
            String cardClassification,
            String merchantName,
            String franchiseName,
            String state,
            String service

    ) {
        this.txnId = txnId;
        this.customTxnId = customTxnId;
        this.actionOnBalance= actionOnBalance;
        this.txnDate = txnDate;
        this.txnAmount = txnAmount;
        this.settleDate = settleDate;
        this.authCode = authCode;
        this.tid = tid;
        this.brandType = brandType;
        this.cardType = cardType;
        this.cardClassification = cardClassification;
        this.merchantName = merchantName;
        this.franchiseName = franchiseName;
        this.state = state;
        this.service = service;

        // Safe assignment - handle nulls
        this.settleAmount = merchantNetAmount != null ? merchantNetAmount : BigDecimal.ZERO;
        this.systemFee = grossCharge != null ? grossCharge : BigDecimal.ZERO;
        this.commissionAmount = franchiseCommission != null ? franchiseCommission : BigDecimal.ZERO;
        // ===================== FIX: PAYOUT → NO COMMISSION =====================
        if ("PAYOUT".equalsIgnoreCase(service) || "PAYOUT_REFUND".equalsIgnoreCase(service)) {

            this.commissionAmount = BigDecimal.ZERO;
            this.commissionRate = null;
            this.franchiseRate = null;
            this.merchantRate = null;
            this.settlementRate = null;
            this.tdsAmount = BigDecimal.ZERO;
            this.gstAmount = BigDecimal.ZERO;
            this.systemFeeExGST = BigDecimal.ZERO;
            this.netCommissionAmount = BigDecimal.ZERO;

            return;
        }
        // =======================================================================

        // Calculate rates only if we have valid data
        if (txnAmount != null && txnAmount.compareTo(BigDecimal.ZERO) > 0
                && merchantNetAmount != null && franchiseCommission != null) {

            // This is a commission transaction (from merchant settlement)
            this.merchantRate = txnAmount
                    .subtract(settleAmount)
                    .divide(txnAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            this.commissionRate = commissionAmount
                    .divide(settleAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            this.franchiseRate = this.merchantRate.subtract(this.commissionRate);
            this.settlementRate = this.merchantRate;
            this.tdsAmount = commissionAmount.multiply(tdsRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            this.tdsPercentage = tdsRate;
            this.gstAmount = grossCharge.multiply(gstRate)
                    .divide(BigDecimal.valueOf(100).add(gstRate), 2, RoundingMode.HALF_UP);
            this.systemFeeExGST = grossCharge.subtract(gstAmount);
            this.netCommissionAmount = commissionAmount.subtract(tdsAmount);
        } else {
            // Standalone franchise transaction (DEBIT/CREDIT not related to merchant)
            this.merchantRate = null;
            this.franchiseRate = null;
            this.commissionRate = null;
            this.settlementRate = null;
        }
    }

    public BigDecimal getSystemFeeExGST() {
        return systemFeeExGST;
    }

    public void setSystemFeeExGST(BigDecimal systemFeeExGST) {
        this.systemFeeExGST = systemFeeExGST;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getTdsAmount() {
        return tdsAmount;
    }

    public void setTdsAmount(BigDecimal tdsAmount) {
        this.tdsAmount = tdsAmount;
    }

    public BigDecimal getTdsPercentage() {
        return tdsPercentage;
    }

    public void setTdsPercentage(BigDecimal tdsPercentage) {
        this.tdsPercentage = tdsPercentage;
    }

    public BigDecimal getNetCommissionAmount() {
        return netCommissionAmount;
    }

    public void setNetCommissionAmount(BigDecimal netCommissionAmount) {
        this.netCommissionAmount = netCommissionAmount;
    }

    public Long getCustomTxnId() {
        return customTxnId;
    }

    public void setCustomTxnId(Long customTxnId) {
        this.customTxnId = customTxnId;
    }

    public String getActionOnBalance() {
        return actionOnBalance;
    }

    public void setActionOnBalance(String actionOnBalance) {
        this.actionOnBalance = actionOnBalance;
    }

    public String getTxnId() { return txnId; }
    public void setTxnId(String txnId) { this.txnId = txnId; }

    public LocalDateTime getTxnDate() { return txnDate; }
    public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }

    public BigDecimal getTxnAmount() { return txnAmount; }
    public void setTxnAmount(BigDecimal txnAmount) { this.txnAmount = txnAmount; }

    public LocalDateTime getSettleDate() { return settleDate; }
    public void setSettleDate(LocalDateTime settleDate) { this.settleDate = settleDate; }

    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }

    public String getTid() { return tid; }
    public void setTid(String tid) { this.tid = tid; }

    public BigDecimal getSettlementRate() { return settlementRate; }
    public void setSettlementRate(BigDecimal settlementPercentage) { this.settlementRate = settlementPercentage; }

    public BigDecimal getSettleAmount() { return settleAmount; }
    public void setSettleAmount(BigDecimal settleAmount) { this.settleAmount = settleAmount; }

    public BigDecimal getSystemFee() { return systemFee; }
    public void setSystemFee(BigDecimal systemFee) { this.systemFee = systemFee; }


    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getFranchiseRate() {
        return franchiseRate;
    }

    public void setFranchiseRate(BigDecimal franchiseRate) {
        this.franchiseRate = franchiseRate;
    }

    public BigDecimal getMerchantRate() {
        return merchantRate;
    }

    public void setMerchantRate(BigDecimal merchantRate) {
        this.merchantRate = merchantRate;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getBrandType() {
        return brandType;
    }

    public void setBrandType(String brandType) {
        this.brandType = brandType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardClassification() { return cardClassification; }
    public void setCardClassification(String cardClassification) { this.cardClassification = cardClassification; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getFranchiseName() { return franchiseName; }
    public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}

