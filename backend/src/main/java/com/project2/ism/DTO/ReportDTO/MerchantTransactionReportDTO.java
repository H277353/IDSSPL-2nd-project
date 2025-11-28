package com.project2.ism.DTO.ReportDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantTransactionReportDTO {
    private Long customTxnId;
    private String txnId;
    private String actionOnBalance;
    private LocalDateTime txnDate;
    private BigDecimal txnAmount;
    private LocalDateTime settleDate;
    private String authCode;
    private String tid;
    private BigDecimal settlementPercentage;
    private BigDecimal settleAmount;
    private BigDecimal systemFee;

    @JsonIgnore  // Don't expose this directly - we use it for role logic
    private BigDecimal grossCharge;

    private BigDecimal merchantRate;
    private BigDecimal franchiseRate;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private BigDecimal gstAmount;
    private BigDecimal systemFeeExGST;
    private String brandType;
    private String cardType;
    private String cardClassification;
    private String merchantName;
    private String franchiseName;
    private String state;
    private String service;

    // Getters and setters...

    // Constructor with raw amounts
    public MerchantTransactionReportDTO(
            Long customTxnId,
            String txnId,
            String actionOnBalance,
            LocalDateTime txnDate,
            BigDecimal txnAmount,
            LocalDateTime settleDate,
            String authCode,
            String tid,
            BigDecimal merchantNetAmount,
            BigDecimal grossCharge,
            BigDecimal franchiseNetAmount,  // THIS is ftd.netAmount (franchise commission)
            BigDecimal charge,              // mtd.charge
            String brandType,
            String cardType,
            String cardClassification,
            String merchantName,
            String franchiseName,
            String state,
            String service
    ) {
        this.customTxnId = customTxnId;
        this.txnId = txnId;
        this.actionOnBalance = actionOnBalance;
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
        // Store raw values (keep grossCharge for role-based logic later)
        this.settleAmount = merchantNetAmount;
        this.systemFee = charge;  // This is what we show by default


        // Store grossCharge internally (we'll use it for merchant view)
        this.grossCharge = grossCharge;  // ADD this field to your DTO

        // ===================== FIX: PAYOUT → NO COMMISSION =====================
        if ("PAYOUT".equalsIgnoreCase(service) || "PAYOUT_REFUND".equalsIgnoreCase(service)) {

            this.commissionAmount = BigDecimal.ZERO;
            this.merchantRate = null;
            this.franchiseRate = null;
            this.commissionRate = null;
            this.settlementPercentage = null;
            return; // STOP here → no further calculation
        }
        this.commissionAmount = franchiseNetAmount;
        // Safe calculations
        if (txnAmount != null && txnAmount.compareTo(BigDecimal.ZERO) > 0 && merchantNetAmount != null) {


            if (franchiseNetAmount != null) {
                // Dependent merchant (with franchise)
                this.merchantRate = txnAmount.subtract(merchantNetAmount)
                        .divide(txnAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                this.commissionRate = commissionAmount
                        .divide(settleAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                this.franchiseRate = this.merchantRate.subtract(this.commissionRate);
                this.settlementPercentage = this.merchantRate;
            } else {
                // Direct merchant (no franchise)
                this.merchantRate = txnAmount.subtract(merchantNetAmount)
                        .divide(txnAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                this.franchiseRate = null;
                this.commissionRate = null;
                this.settlementPercentage = this.merchantRate;
            }
        } else {
            this.merchantRate = null;
            this.franchiseRate = null;
            this.commissionRate = null;
            this.settlementPercentage = null;
        }

    }
    private static BigDecimal nullSafe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private static BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator) {
        if (numerator == null || denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return numerator.divide(denominator, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
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

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public LocalDateTime getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(LocalDateTime txnDate) {
        this.txnDate = txnDate;
    }

    public BigDecimal getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(BigDecimal txnAmount) {
        this.txnAmount = txnAmount;
    }

    public LocalDateTime getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(LocalDateTime settleDate) {
        this.settleDate = settleDate;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public BigDecimal getSettlementPercentage() {
        return settlementPercentage;
    }

    public void setSettlementPercentage(BigDecimal settlementPercentage) {
        this.settlementPercentage = settlementPercentage;
    }

    public BigDecimal getGrossCharge() {
        return grossCharge;
    }

    public void setGrossCharge(BigDecimal grossCharge) {
        this.grossCharge = grossCharge;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public BigDecimal getSystemFee() {
        return systemFee;
    }

    public void setSystemFee(BigDecimal systemFee) {
        this.systemFee = systemFee;
    }

    public BigDecimal getMerchantRate() {
        return merchantRate;
    }

    public void setMerchantRate(BigDecimal merchantRate) {
        this.merchantRate = merchantRate;
    }

    public BigDecimal getFranchiseRate() {
        return franchiseRate;
    }

    public void setFranchiseRate(BigDecimal franchiseRate) {
        this.franchiseRate = franchiseRate;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
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

    public String getCardClassification() {
        return cardClassification;
    }

    public void setCardClassification(String cardClassification) {
        this.cardClassification = cardClassification;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getFranchiseName() {
        return franchiseName;
    }

    public void setFranchiseName(String franchiseName) {
        this.franchiseName = franchiseName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
