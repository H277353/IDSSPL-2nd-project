package com.project2.ism.DTO.ReportDTO;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionReportDTO {

    // Request DTOs
    public static class TransactionReportRequest {
        @NotNull(message = "Start date is required")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startDate;

        @NotNull(message = "End date is required")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endDate;

        private Long merchantId;
        private Long franchiseId;
        private String transactionStatus;
        private String transactionType;
        private int page = 0;
        private int size = 50;
        // NEW FIELDS - Add these to existing TransactionReportRequest
        private String dateFilterType = "TRANSACTION_DATE"; // TRANSACTION_DATE or SETTLEMENT_DATE
        private String merchantType = "ALL"; // ALL, DIRECT, FRANCHISE

        // Generate: getters, setters, constructors (noargs, allargs)

        public TransactionReportRequest() {
        }

        public TransactionReportRequest(LocalDateTime startDate, LocalDateTime endDate, Long merchantId, Long franchiseId, String transactionStatus, String transactionType, int page, int size) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.merchantId = merchantId;
            this.franchiseId = franchiseId;
            this.transactionStatus = transactionStatus;
            this.transactionType = transactionType;
            this.page = page;
            this.size = size;
        }

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }

        public Long getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(Long merchantId) {
            this.merchantId = merchantId;
        }

        public Long getFranchiseId() {
            return franchiseId;
        }

        public void setFranchiseId(Long franchiseId) {
            this.franchiseId = franchiseId;
        }

        public String getTransactionStatus() {
            return transactionStatus;
        }

        public void setTransactionStatus(String transactionStatus) {
            this.transactionStatus = transactionStatus;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
        public String getDateFilterType() {
            return dateFilterType;
        }

        public void setDateFilterType(String dateFilterType) {
            this.dateFilterType = dateFilterType;
        }

        public String getMerchantType() {
            return merchantType;
        }

        public void setMerchantType(String merchantType) {
            this.merchantType = merchantType;
        }
    }

    // Response DTOs
    public static class TransactionDetailResponse {
        private Long transactionId;
        private LocalDateTime transactionDate;
        private BigDecimal amount;
        private BigDecimal netAmount;
        private BigDecimal charge;
        private String vendorName;
        private String transactionType;
        private String status;
        private String narration;
        private String cardHolderName;
        private String mobileNo;
        private String bankRefId;
        private String operatorName;
        private String remarks;

        // Generate: getters, setters, constructors (noargs, allargs)

        public TransactionDetailResponse() {
        }

        public TransactionDetailResponse(Long transactionId, LocalDateTime transactionDate, BigDecimal amount, BigDecimal netAmount, BigDecimal charge, String vendorName, String transactionType, String status, String narration, String cardHolderName, String mobileNo, String bankRefId, String operatorName, String remarks) {
            this.transactionId = transactionId;
            this.transactionDate = transactionDate;
            this.amount = amount;
            this.netAmount = netAmount;
            this.charge = charge;
            this.vendorName = vendorName;
            this.transactionType = transactionType;
            this.status = status;
            this.narration = narration;
            this.cardHolderName = cardHolderName;
            this.mobileNo = mobileNo;
            this.bankRefId = bankRefId;
            this.operatorName = operatorName;
            this.remarks = remarks;
        }

        public Long getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(Long transactionId) {
            this.transactionId = transactionId;
        }

        public LocalDateTime getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(BigDecimal netAmount) {
            this.netAmount = netAmount;
        }

        public BigDecimal getCharge() {
            return charge;
        }

        public void setCharge(BigDecimal charge) {
            this.charge = charge;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNarration() {
            return narration;
        }

        public void setNarration(String narration) {
            this.narration = narration;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getBankRefId() {
            return bankRefId;
        }

        public void setBankRefId(String bankRefId) {
            this.bankRefId = bankRefId;
        }

        public String getOperatorName() {
            return operatorName;
        }

        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    public static class TransactionSummary {
        private Long totalTransactions;
        private BigDecimal totalAmount;
        private BigDecimal totalNetAmount;
        private BigDecimal totalCharges;
        private BigDecimal totalCommission;
        private BigDecimal averageTransactionValue;
        private Long successfulTransactions;
        private Long failedTransactions;
        private Double successRate;

        // Generate: getters, setters, constructors (noargs, allargs)

        public TransactionSummary() {
        }

        public TransactionSummary(Long totalTransactions, BigDecimal totalAmount, BigDecimal totalNetAmount, BigDecimal totalCharges, BigDecimal totalCommission, BigDecimal averageTransactionValue, Long successfulTransactions, Long failedTransactions, Double successRate) {
            this.totalTransactions = totalTransactions;
            this.totalAmount = totalAmount;
            this.totalNetAmount = totalNetAmount;
            this.totalCharges = totalCharges;
            this.totalCommission = totalCommission;
            this.averageTransactionValue = averageTransactionValue;
            this.successfulTransactions = successfulTransactions;
            this.failedTransactions = failedTransactions;
            this.successRate = successRate;
        }

        public Long getTotalTransactions() {
            return totalTransactions;
        }

        public void setTotalTransactions(Long totalTransactions) {
            this.totalTransactions = totalTransactions;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getTotalNetAmount() {
            return totalNetAmount;
        }

        public void setTotalNetAmount(BigDecimal totalNetAmount) {
            this.totalNetAmount = totalNetAmount;
        }

        public BigDecimal getTotalCharges() {
            return totalCharges;
        }

        public void setTotalCharges(BigDecimal totalCharges) {
            this.totalCharges = totalCharges;
        }

        public BigDecimal getTotalCommission() {
            return totalCommission;
        }

        public void setTotalCommission(BigDecimal totalCommission) {
            this.totalCommission = totalCommission;
        }

        public BigDecimal getAverageTransactionValue() {
            return averageTransactionValue;
        }

        public void setAverageTransactionValue(BigDecimal averageTransactionValue) {
            this.averageTransactionValue = averageTransactionValue;
        }

        public Long getSuccessfulTransactions() {
            return successfulTransactions;
        }

        public void setSuccessfulTransactions(Long successfulTransactions) {
            this.successfulTransactions = successfulTransactions;
        }

        public Long getFailedTransactions() {
            return failedTransactions;
        }

        public void setFailedTransactions(Long failedTransactions) {
            this.failedTransactions = failedTransactions;
        }

        public Double getSuccessRate() {
            return successRate;
        }

        public void setSuccessRate(Double successRate) {
            this.successRate = successRate;
        }
    }

    public static class TransactionReportResponse<T> {
        private List<T> transactions;
        private Object summary;
        private LocalDateTime reportGeneratedAt;
        private String reportType;
        private int totalPages;
        private long totalElements;
        private boolean hasNext;
        private boolean hasPrevious;

        // Generate: getters, setters, constructors (noargs, allargs)

        public TransactionReportResponse() {
        }

        public TransactionReportResponse(List<T> transactions, TransactionSummary summary, LocalDateTime reportGeneratedAt, String reportType, int totalPages, long totalElements, boolean hasNext, boolean hasPrevious) {
            this.transactions = transactions;
            this.summary = summary;
            this.reportGeneratedAt = reportGeneratedAt;
            this.reportType = reportType;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        public List<T> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<T> transactions) {
            this.transactions = transactions;
        }

        public Object getSummary() {
            return summary;
        }

        public void setSummary(Object summary) {
            this.summary = summary;
        }

        public LocalDateTime getReportGeneratedAt() {
            return reportGeneratedAt;
        }

        public void setReportGeneratedAt(LocalDateTime reportGeneratedAt) {
            this.reportGeneratedAt = reportGeneratedAt;
        }

        public String getReportType() {
            return reportType;
        }

        public void setReportType(String reportType) {
            this.reportType = reportType;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }

    public static class CommissionBreakdown {
        private BigDecimal grossCommission;
        private BigDecimal netCommission;
        private BigDecimal commissionRate;
        private Long merchantCount;
        private BigDecimal averageCommissionPerMerchant;

        // Generate: getters, setters, constructors (noargs, allargs)

        public CommissionBreakdown() {
        }

        public CommissionBreakdown(BigDecimal grossCommission, BigDecimal netCommission, BigDecimal commissionRate, Long merchantCount, BigDecimal averageCommissionPerMerchant) {
            this.grossCommission = grossCommission;
            this.netCommission = netCommission;
            this.commissionRate = commissionRate;
            this.merchantCount = merchantCount;
            this.averageCommissionPerMerchant = averageCommissionPerMerchant;
        }

        public BigDecimal getGrossCommission() {
            return grossCommission;
        }

        public void setGrossCommission(BigDecimal grossCommission) {
            this.grossCommission = grossCommission;
        }

        public BigDecimal getNetCommission() {
            return netCommission;
        }

        public void setNetCommission(BigDecimal netCommission) {
            this.netCommission = netCommission;
        }

        public BigDecimal getCommissionRate() {
            return commissionRate;
        }

        public void setCommissionRate(BigDecimal commissionRate) {
            this.commissionRate = commissionRate;
        }

        public Long getMerchantCount() {
            return merchantCount;
        }

        public void setMerchantCount(Long merchantCount) {
            this.merchantCount = merchantCount;
        }

        public BigDecimal getAverageCommissionPerMerchant() {
            return averageCommissionPerMerchant;
        }

        public void setAverageCommissionPerMerchant(BigDecimal averageCommissionPerMerchant) {
            this.averageCommissionPerMerchant = averageCommissionPerMerchant;
        }
    }

    public static class FranchiseTransactionSummary extends TransactionSummary {
        private CommissionBreakdown commissionBreakdown;
        private Long activeMerchants;

        // Generate: getters, setters, constructors (noargs, allargs)

        public FranchiseTransactionSummary() {

        }



        public CommissionBreakdown getCommissionBreakdown() {
            return commissionBreakdown;
        }

        public void setCommissionBreakdown(CommissionBreakdown commissionBreakdown) {
            this.commissionBreakdown = commissionBreakdown;
        }

        public Long getActiveMerchants() {
            return activeMerchants;
        }

        public void setActiveMerchants(Long activeMerchants) {
            this.activeMerchants = activeMerchants;
        }
    }


    /**
     * DTO for detailed transaction report
     */
    public static class DetailedTransactionReportDTO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime txnDate;

        private BigDecimal txnAmount;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime settleDate;

        private String authCode;
        private String tid;
        private Double settlementPercentage;
        private BigDecimal settleAmount;
        private BigDecimal retailorMDR;
        private Double retailorPercentage;
        private BigDecimal commissionAmount;
        private String cardType;
        private String brandType;
        private String cardClassification;
        private String merchantName;
        private String franchiseName;
        private String state;

        // Constructors
        public DetailedTransactionReportDTO() {}

        public DetailedTransactionReportDTO(LocalDateTime txnDate, BigDecimal txnAmount, LocalDateTime settleDate,
                                            String authCode, String tid, Double settlementPercentage,
                                            BigDecimal settleAmount, BigDecimal retailorMDR, Double retailorPercentage,
                                            BigDecimal commissionAmount, String cardType, String brandType,
                                            String cardClassification, String merchantName, String franchiseName, String state) {
            this.txnDate = txnDate;
            this.txnAmount = txnAmount;
            this.settleDate = settleDate;
            this.authCode = authCode;
            this.tid = tid;
            this.settlementPercentage = settlementPercentage;
            this.settleAmount = settleAmount;
            this.retailorMDR = retailorMDR;
            this.retailorPercentage = retailorPercentage;
            this.commissionAmount = commissionAmount;
            this.cardType = cardType;
            this.brandType = brandType;
            this.cardClassification = cardClassification;
            this.merchantName = merchantName;
            this.franchiseName = franchiseName;
            this.state = state;
        }

        // Getters and Setters
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

        public Double getSettlementPercentage() { return settlementPercentage; }
        public void setSettlementPercentage(Double settlementPercentage) { this.settlementPercentage = settlementPercentage; }

        public BigDecimal getSettleAmount() { return settleAmount; }
        public void setSettleAmount(BigDecimal settleAmount) { this.settleAmount = settleAmount; }

        public BigDecimal getRetailorMDR() { return retailorMDR; }
        public void setRetailorMDR(BigDecimal retailorMDR) { this.retailorMDR = retailorMDR; }

        public Double getRetailorPercentage() { return retailorPercentage; }
        public void setRetailorPercentage(Double retailorPercentage) { this.retailorPercentage = retailorPercentage; }

        public BigDecimal getCommissionAmount() { return commissionAmount; }
        public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

        public String getCardType() { return cardType; }
        public void setCardType(String cardType) { this.cardType = cardType; }

        public String getBrandType() { return brandType; }
        public void setBrandType(String brandType) { this.brandType = brandType; }

        public String getCardClassification() { return cardClassification; }
        public void setCardClassification(String cardClassification) { this.cardClassification = cardClassification; }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public String getFranchiseName() { return franchiseName; }
        public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    /**
     * DTO for card type and brand summary
     */
    public static class CardTypeBrandSummaryDTO {
        private String cardType;
        private String brandType;
        private Long transactionCount;
        private BigDecimal totalAmount;
        private BigDecimal totalSettleAmount;
        private BigDecimal totalCommission;
        private BigDecimal totalMDR;
        private BigDecimal averageAmount;
        private Double averageMDRPercentage;

        // Constructors
        public CardTypeBrandSummaryDTO() {}

        public CardTypeBrandSummaryDTO(String cardType, String brandType, Long transactionCount,
                                       BigDecimal totalAmount, BigDecimal totalSettleAmount,
                                       BigDecimal totalCommission, BigDecimal totalMDR,
                                       BigDecimal averageAmount, Double averageMDRPercentage) {
            this.cardType = cardType;
            this.brandType = brandType;
            this.transactionCount = transactionCount;
            this.totalAmount = totalAmount;
            this.totalSettleAmount = totalSettleAmount;
            this.totalCommission = totalCommission;
            this.totalMDR = totalMDR;
            this.averageAmount = averageAmount;
            this.averageMDRPercentage = averageMDRPercentage;
        }

        // Getters and Setters
        public String getCardType() { return cardType; }
        public void setCardType(String cardType) { this.cardType = cardType; }

        public String getBrandType() { return brandType; }
        public void setBrandType(String brandType) { this.brandType = brandType; }

        public Long getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getTotalSettleAmount() { return totalSettleAmount; }
        public void setTotalSettleAmount(BigDecimal totalSettleAmount) { this.totalSettleAmount = totalSettleAmount; }

        public BigDecimal getTotalCommission() { return totalCommission; }
        public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

        public BigDecimal getTotalMDR() { return totalMDR; }
        public void setTotalMDR(BigDecimal totalMDR) { this.totalMDR = totalMDR; }

        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }

        public Double getAverageMDRPercentage() { return averageMDRPercentage; }
        public void setAverageMDRPercentage(Double averageMDRPercentage) { this.averageMDRPercentage = averageMDRPercentage; }
    }

    /**
     * DTO for daily summary report
     */
    public static class DailySummaryReportDTO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime txnDate;

        private Long totalTransactions;
        private BigDecimal totalAmount;
        private BigDecimal totalSettleAmount;
        private BigDecimal totalCommission;
        private BigDecimal totalMDR;
        private Long settledCount;
        private Long failedCount;
        private BigDecimal averageAmount;
        private Long uniqueMerchants;

        // Constructors
        public DailySummaryReportDTO() {}

        public DailySummaryReportDTO(LocalDateTime txnDate, Long totalTransactions, BigDecimal totalAmount,
                                     BigDecimal totalSettleAmount, BigDecimal totalCommission, BigDecimal totalMDR,
                                     Long settledCount, Long failedCount, BigDecimal averageAmount, Long uniqueMerchants) {
            this.txnDate = txnDate;
            this.totalTransactions = totalTransactions;
            this.totalAmount = totalAmount;
            this.totalSettleAmount = totalSettleAmount;
            this.totalCommission = totalCommission;
            this.totalMDR = totalMDR;
            this.settledCount = settledCount;
            this.failedCount = failedCount;
            this.averageAmount = averageAmount;
            this.uniqueMerchants = uniqueMerchants;
        }

        // Getters and Setters
        public LocalDateTime getTxnDate() { return txnDate; }
        public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }

        public Long getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getTotalSettleAmount() { return totalSettleAmount; }
        public void setTotalSettleAmount(BigDecimal totalSettleAmount) { this.totalSettleAmount = totalSettleAmount; }

        public BigDecimal getTotalCommission() { return totalCommission; }
        public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

        public BigDecimal getTotalMDR() { return totalMDR; }
        public void setTotalMDR(BigDecimal totalMDR) { this.totalMDR = totalMDR; }

        public Long getSettledCount() { return settledCount; }
        public void setSettledCount(Long settledCount) { this.settledCount = settledCount; }

        public Long getFailedCount() { return failedCount; }
        public void setFailedCount(Long failedCount) { this.failedCount = failedCount; }

        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }

        public Long getUniqueMerchants() { return uniqueMerchants; }
        public void setUniqueMerchants(Long uniqueMerchants) { this.uniqueMerchants = uniqueMerchants; }
    }

    /**
     * DTO for merchant performance
     */
    public static class MerchantPerformanceDTO {
        private Long merchantId;
        private String merchantName;
        private Long transactionCount;
        private BigDecimal totalAmount;
        private BigDecimal totalSettleAmount;
        private BigDecimal totalCommission;
        private BigDecimal totalMDR;
        private BigDecimal averageAmount;
        private Long successCount;
        private Long failureCount;
        private Double successRate;

        // Constructors
        public MerchantPerformanceDTO() {}

        public MerchantPerformanceDTO(Long merchantId, String merchantName, Long transactionCount,
                                      BigDecimal totalAmount, BigDecimal totalSettleAmount,
                                      BigDecimal totalCommission, BigDecimal totalMDR, BigDecimal averageAmount,
                                      Long successCount, Long failureCount, Double successRate) {
            this.merchantId = merchantId;
            this.merchantName = merchantName;
            this.transactionCount = transactionCount;
            this.totalAmount = totalAmount;
            this.totalSettleAmount = totalSettleAmount;
            this.totalCommission = totalCommission;
            this.totalMDR = totalMDR;
            this.averageAmount = averageAmount;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.successRate = successRate;
        }

        // Getters and Setters
        public Long getMerchantId() { return merchantId; }
        public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public Long getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getTotalSettleAmount() { return totalSettleAmount; }
        public void setTotalSettleAmount(BigDecimal totalSettleAmount) { this.totalSettleAmount = totalSettleAmount; }

        public BigDecimal getTotalCommission() { return totalCommission; }
        public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

        public BigDecimal getTotalMDR() { return totalMDR; }
        public void setTotalMDR(BigDecimal totalMDR) { this.totalMDR = totalMDR; }

        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }

        public Long getSuccessCount() { return successCount; }
        public void setSuccessCount(Long successCount) { this.successCount = successCount; }

        public Long getFailureCount() { return failureCount; }
        public void setFailureCount(Long failureCount) { this.failureCount = failureCount; }

        public Double getSuccessRate() { return successRate; }
        public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    }

    /**
     * DTO for franchise comparison
     */
    public static class FranchiseComparisonDTO {
        private Long franchiseId;
        private String franchiseName;
        private String state;
        private Long transactionCount;
        private BigDecimal totalAmount;
        private BigDecimal totalSettleAmount;
        private BigDecimal totalCommission;
        private Long uniqueMerchants;
        private BigDecimal averageAmount;
        private Long successCount;
        private Double successRate;

        // Constructors
        public FranchiseComparisonDTO() {}

        public FranchiseComparisonDTO(Long franchiseId, String franchiseName, String state,
                                      Long transactionCount, BigDecimal totalAmount, BigDecimal totalSettleAmount,
                                      BigDecimal totalCommission, Long uniqueMerchants, BigDecimal averageAmount,
                                      Long successCount, Double successRate) {
            this.franchiseId = franchiseId;
            this.franchiseName = franchiseName;
            this.state = state;
            this.transactionCount = transactionCount;
            this.totalAmount = totalAmount;
            this.totalSettleAmount = totalSettleAmount;
            this.totalCommission = totalCommission;
            this.uniqueMerchants = uniqueMerchants;
            this.averageAmount = averageAmount;
            this.successCount = successCount;
            this.successRate = successRate;
        }

        // Getters and Setters
        public Long getFranchiseId() { return franchiseId; }
        public void setFranchiseId(Long franchiseId) { this.franchiseId = franchiseId; }

        public String getFranchiseName() { return franchiseName; }
        public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public Long getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getTotalSettleAmount() { return totalSettleAmount; }
        public void setTotalSettleAmount(BigDecimal totalSettleAmount) { this.totalSettleAmount = totalSettleAmount; }

        public BigDecimal getTotalCommission() { return totalCommission; }
        public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

        public Long getUniqueMerchants() { return uniqueMerchants; }
        public void setUniqueMerchants(Long uniqueMerchants) { this.uniqueMerchants = uniqueMerchants; }

        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }

        public Long getSuccessCount() { return successCount; }
        public void setSuccessCount(Long successCount) { this.successCount = successCount; }

        public Double getSuccessRate() { return successRate; }
        public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    }

    /**
     * DTO for terminal analysis
     */
    public static class TerminalAnalysisDTO {
        private String terminalId;
        private String merchantName;
        private String franchiseName;
        private Long transactionCount;
        private BigDecimal totalAmount;
        private BigDecimal totalSettleAmount;
        private BigDecimal totalCommission;
        private BigDecimal averageAmount;
        private Long successCount;
        private Long failureCount;

        // Constructors
        public TerminalAnalysisDTO() {}

        public TerminalAnalysisDTO(String terminalId, String merchantName, String franchiseName,
                                   Long transactionCount, BigDecimal totalAmount, BigDecimal totalSettleAmount,
                                   BigDecimal totalCommission, BigDecimal averageAmount, Long successCount, Long failureCount) {
            this.terminalId = terminalId;
            this.merchantName = merchantName;
            this.franchiseName = franchiseName;
            this.transactionCount = transactionCount;
            this.totalAmount = totalAmount;
            this.totalSettleAmount = totalSettleAmount;
            this.totalCommission = totalCommission;
            this.averageAmount = averageAmount;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        // Getters and Setters
        public String getTerminalId() { return terminalId; }
        public void setTerminalId(String terminalId) { this.terminalId = terminalId; }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public String getFranchiseName() { return franchiseName; }
        public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }

        public Long getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getTotalSettleAmount() { return totalSettleAmount; }
        public void setTotalSettleAmount(BigDecimal totalSettleAmount) { this.totalSettleAmount = totalSettleAmount; }

        public BigDecimal getTotalCommission() { return totalCommission; }
        public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }

        public Long getSuccessCount() { return successCount; }
        public void setSuccessCount(Long successCount) { this.successCount = successCount; }

        public Long getFailureCount() { return failureCount; }
        public void setFailureCount(Long failureCount) { this.failureCount = failureCount; }
    }

    /**
     * DTO for hourly trend
     */
    public static class HourlyTrendDTO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime txnDate;

        private Integer txnHour;
        private Long transactionCount;
        private BigDecimal totalAmount;
        private BigDecimal averageAmount;
        private Long successCount;

        // Constructors
        public HourlyTrendDTO() {}

        public HourlyTrendDTO(LocalDateTime txnDate, Integer txnHour, Long transactionCount,
                              BigDecimal totalAmount, BigDecimal averageAmount, Long successCount) {
            this.txnDate = txnDate;
            this.txnHour = txnHour;
            this.transactionCount = transactionCount;
            this.totalAmount = totalAmount;
            this.averageAmount = averageAmount;
            this.successCount = successCount;
        }

        // Getters and Setters
        public LocalDateTime getTxnDate() { return txnDate; }
        public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }

        public Integer getTxnHour() { return txnHour; }
        public void setTxnHour(Integer txnHour) { this.txnHour = txnHour; }

        public Long getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }

        public Long getSuccessCount() { return successCount; }
        public void setSuccessCount(Long successCount) { this.successCount = successCount; }
    }

    /**
     * DTO for failed transaction analysis
     */
    public static class FailedTransactionDTO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime transactionDate;

        private BigDecimal amount;
        private String tranStatus;
        private String failureRemarks;
        private String errorCode;
        private String pgErrorMessage;
        private String tid;
        private String cardType;
        private String brandType;
        private String merchantName;
        private String franchiseName;

        // Constructors
        public FailedTransactionDTO() {}

        public FailedTransactionDTO(LocalDateTime transactionDate, BigDecimal amount, String tranStatus,
                                    String failureRemarks, String errorCode, String pgErrorMessage, String tid,
                                    String cardType, String brandType, String merchantName, String franchiseName) {
            this.transactionDate = transactionDate;
            this.amount = amount;
            this.tranStatus = tranStatus;
            this.failureRemarks = failureRemarks;
            this.errorCode = errorCode;
            this.pgErrorMessage = pgErrorMessage;
            this.tid = tid;
            this.cardType = cardType;
            this.brandType = brandType;
            this.merchantName = merchantName;
            this.franchiseName = franchiseName;
        }

        // Getters and Setters
        public LocalDateTime getTransactionDate() { return transactionDate; }
        public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getTranStatus() { return tranStatus; }
        public void setTranStatus(String tranStatus) { this.tranStatus = tranStatus; }

        public String getFailureRemarks() { return failureRemarks; }
        public void setFailureRemarks(String failureRemarks) { this.failureRemarks = failureRemarks; }

        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getPgErrorMessage() { return pgErrorMessage; }
        public void setPgErrorMessage(String pgErrorMessage) { this.pgErrorMessage = pgErrorMessage; }

        public String getTid() { return tid; }
        public void setTid(String tid) { this.tid = tid; }

        public String getCardType() { return cardType; }
        public void setCardType(String cardType) { this.cardType = cardType; }

        public String getBrandType() { return brandType; }
        public void setBrandType(String brandType) { this.brandType = brandType; }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public String getFranchiseName() { return franchiseName; }
        public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }
    }

    /**
     * DTO for settlement delay analysis
     */
    public static class SettlementDelayAnalysisDTO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime transactionDate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime settlementDate;

        private Long settlementDelayHours;
        private BigDecimal amount;
        private BigDecimal netAmount;
        private String tid;
        private String merchantName;
        private String franchiseName;

        // Constructors
        public SettlementDelayAnalysisDTO() {}

        public SettlementDelayAnalysisDTO(LocalDateTime transactionDate, LocalDateTime settlementDate,
                                          Long settlementDelayHours, BigDecimal amount, BigDecimal netAmount,
                                          String tid, String merchantName, String franchiseName) {
            this.transactionDate = transactionDate;
            this.settlementDate = settlementDate;
            this.settlementDelayHours = settlementDelayHours;
            this.amount = amount;
            this.netAmount = netAmount;
            this.tid = tid;
            this.merchantName = merchantName;
            this.franchiseName = franchiseName;
        }

        // Getters and Setters
        public LocalDateTime getTransactionDate() { return transactionDate; }
        public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

        public LocalDateTime getSettlementDate() { return settlementDate; }
        public void setSettlementDate(LocalDateTime settlementDate) { this.settlementDate = settlementDate; }

        public Long getSettlementDelayHours() { return settlementDelayHours; }
        public void setSettlementDelayHours(Long settlementDelayHours) { this.settlementDelayHours = settlementDelayHours; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public BigDecimal getNetAmount() { return netAmount; }
        public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }

        public String getTid() { return tid; }
        public void setTid(String tid) { this.tid = tid; }

        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

        public String getFranchiseName() { return franchiseName; }
        public void setFranchiseName(String franchiseName) { this.franchiseName = franchiseName; }
    }

    /**
     * DTO for transaction summary
     */
    public static class TransactionSummaryDTO {
        private Long totalTransactions;
        private BigDecimal totalAmount;
        private BigDecimal totalCommission;
        private BigDecimal totalNetAmount;
        private BigDecimal averageAmount;
        private Long successCount;
        private Long failureCount;
        private Long activeMerchants;

        // Constructors
        public TransactionSummaryDTO() {}

        public TransactionSummaryDTO(Long totalTransactions, BigDecimal totalAmount, BigDecimal totalCommission,
                                     BigDecimal totalNetAmount, BigDecimal averageAmount, Long successCount,
                                     Long failureCount, Long activeMerchants) {
            this.totalTransactions = totalTransactions;
            this.totalAmount = totalAmount;
            this.totalCommission = totalCommission;
            this.totalNetAmount = totalNetAmount;
            this.averageAmount = averageAmount;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.activeMerchants = activeMerchants;
        }

        // Getters and Setters
        public Long getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getTotalCommission() { return totalCommission; }
        public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }

        public BigDecimal getTotalNetAmount() { return totalNetAmount; }
        public void setTotalNetAmount(BigDecimal totalNetAmount) { this.totalNetAmount = totalNetAmount; }

        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }

        public Long getSuccessCount() { return successCount; }
        public void setSuccessCount(Long successCount) { this.successCount = successCount; }

        public Long getFailureCount() { return failureCount; }
        public void setFailureCount(Long failureCount) { this.failureCount = failureCount; }

        public Long getActiveMerchants() { return activeMerchants; }
        public void setActiveMerchants(Long activeMerchants) { this.activeMerchants = activeMerchants; }
    }
}