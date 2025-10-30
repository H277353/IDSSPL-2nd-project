package com.project2.ism.Controller;

import com.project2.ism.DTO.ReportDTO.ApiResponse;
import com.project2.ism.DTO.ReportDTO.TransactionReportDTO;

import com.project2.ism.DTO.ReportDTO.TransactionReportDTO.*;
import com.project2.ism.Exception.BusinessException;
import com.project2.ism.Service.TransactionReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Parameter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/reports/transactions")
@Validated
public class TransactionReportController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionReportController.class);

    private final TransactionReportService transactionReportService;

    public TransactionReportController(TransactionReportService transactionReportService) {
        this.transactionReportService = transactionReportService;
    }



// NEW ENDPOINTS - Add these to your existing TransactionReportController.java

    /**
     * Generate enhanced merchant transaction report with date filter and merchant type options
     * GET /api/v1/reports/transactions/merchant/enhanced
     */
    @GetMapping("/merchant/enhanced")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('MERCHANT') or hasRole('FRANCHISE')")
    public ResponseEntity<ApiResponse<TransactionReportResponse>> generateEnhancedMerchantTransactionReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "merchantId") Long merchantId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "50") @Min(1) @Max(1000) Integer size) {

        logger.info("Received request for enhanced merchant transaction report: startDate={}, endDate={}, merchantId={}, dateFilter={}, ",
                startDate, endDate, merchantId, dateFilterType);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setMerchantId(merchantId);
            request.setTransactionStatus(status);
            request.setTransactionType(transactionType);
            request.setDateFilterType(dateFilterType);
            request.setPage(page);
            request.setSize(size);

            TransactionReportResponse report = transactionReportService.generateEnhancedMerchantTransactionReport(request);

            ApiResponse<TransactionReportResponse> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Merchant transaction report generated successfully");
            response.setData(report);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating enhanced merchant transaction report", e);
            ApiResponse<TransactionReportResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to generate enhanced report: " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Generate enhanced franchise transaction report with date filter options
     * GET /api/v1/reports/transactions/franchise/enhanced
     */
    @GetMapping("/franchise/enhanced")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('FRANCHISE')")
    public ResponseEntity<ApiResponse<TransactionReportResponse>> generateEnhancedFranchiseTransactionReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "franchiseId") Long franchiseId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "50") @Min(1) @Max(1000) Integer size) {

        logger.info("Received request for enhanced franchise transaction report: startDate={}, endDate={}, franchiseId={}, dateFilter={}",
                startDate, endDate, franchiseId, dateFilterType);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setFranchiseId(franchiseId);
            request.setTransactionStatus(status);
            request.setTransactionType(transactionType);
            request.setDateFilterType(dateFilterType);
            request.setPage(page);
            request.setSize(size);

            TransactionReportResponse report = transactionReportService.generateEnhancedFranchiseTransactionReport(request);

            ApiResponse<TransactionReportResponse> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Franchise transaction report generated successfully");
            response.setData(report);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error generating enhanced franchise transaction report", e);
            ApiResponse<TransactionReportResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to generate enhanced report: " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**
     * Get enhanced merchant transaction summary with date filter and merchant type options
     * GET /api/v1/reports/transactions/merchant/summary/enhanced
     */
    @GetMapping("/merchant/summary/enhanced")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('MERCHANT') or hasRole('FRANCHISE')")
    public ResponseEntity<ApiResponse<TransactionSummary>> getEnhancedMerchantTransactionSummary(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "merchantId") Long merchantId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType)
             {

        logger.info("Received request for enhanced merchant transaction summary: startDate={}, endDate={}, merchantId={}, dateFilter={}",
                startDate, endDate, merchantId, dateFilterType);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setMerchantId(merchantId);
            request.setTransactionStatus(status);
            request.setTransactionType(transactionType);
            request.setDateFilterType(dateFilterType);

            TransactionSummary summary = transactionReportService.getEnhancedMerchantTransactionSummary(request);

            ApiResponse<TransactionSummary> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Merchant transaction summary retrieved successfully");
            response.setData(summary);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting enhanced merchant transaction summary", e);
            ApiResponse<TransactionSummary> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to get enhanced summary: " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get enhanced franchise transaction summary with date filter options
     * GET /api/v1/reports/transactions/franchise/summary/enhanced
     */
    @GetMapping("/franchise/summary/enhanced")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('FRANCHISE')")
    public ResponseEntity<ApiResponse<FranchiseTransactionSummary>> getEnhancedFranchiseTransactionSummary(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "franchiseId", required = false) Long franchiseId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType) {

        logger.info("Received request for enhanced franchise transaction summary: startDate={}, endDate={}, franchiseId={}, dateFilter={}",
                startDate, endDate, franchiseId, dateFilterType);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setFranchiseId(franchiseId);
            request.setTransactionStatus(status);
            request.setTransactionType(transactionType);
            request.setDateFilterType(dateFilterType);

            FranchiseTransactionSummary summary = transactionReportService.getEnhancedFranchiseTransactionSummary(request);

            ApiResponse<FranchiseTransactionSummary> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Franchise transaction summary retrieved successfully");
            response.setData(summary);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting enhanced franchise transaction summary", e);
            ApiResponse<FranchiseTransactionSummary> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to get enhanced summary: " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get enhanced franchise merchant performance breakdown with date filter options
     * GET /api/v1/reports/transactions/franchise/merchant-performance/enhanced
     */
    @GetMapping("/franchise/merchant-performance/enhanced")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('FRANCHISE')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEnhancedFranchiseMerchantPerformance(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "franchiseId", required = false) Long franchiseId,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType) {

        logger.info("Received request for enhanced franchise merchant performance: startDate={}, endDate={}, franchiseId={}, dateFilter={}",
                startDate, endDate, franchiseId, dateFilterType);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setFranchiseId(franchiseId);
            request.setDateFilterType(dateFilterType);

            List<Map<String, Object>> performance = transactionReportService.getEnhancedFranchiseMerchantPerformance(request);

            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Franchise merchant performance retrieved successfully");
            response.setData(performance);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting enhanced franchise merchant performance", e);
            ApiResponse<List<Map<String, Object>>> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to get enhanced performance data: " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

//    /**
//     * Get direct merchants report (merchants with franchise_id = null)
//     * GET /api/v1/reports/transactions/direct-merchants
//     */
//    @GetMapping("/direct-merchants")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<TransactionReportResponse>> getDirectMerchantsReport(
//            @RequestParam("startDate") String startDate,
//            @RequestParam("endDate") String endDate,
//            @RequestParam(value = "status", required = false) String status,
//            @RequestParam(value = "transactionType", required = false) String transactionType,
//            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
//            @RequestParam(value = "size", defaultValue = "50") @Min(1) @Max(1000) Integer size) {
//
//        logger.info("Received request for direct merchants report: startDate={}, endDate={}",
//                startDate, endDate);
//
//        try {
//            TransactionReportRequest request = new TransactionReportRequest();
//            request.setStartDate(LocalDateTime.parse(startDate));
//            request.setEndDate(LocalDateTime.parse(endDate));
//            request.setTransactionStatus(status);
//            request.setTransactionType(transactionType);
//            request.setMerchantType("DIRECT");
//            request.setPage(page);
//            request.setSize(size);
//
//            TransactionReportResponse report = transactionReportService.generateEnhancedMerchantTransactionReport(request);
//
//            ApiResponse<TransactionReportResponse> response = new ApiResponse<>();
//            response.setSuccess(true);
//            response.setMessage("Direct merchants report generated successfully");
//            response.setData(report);
//            response.setTimestamp(LocalDateTime.now());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            logger.error("Error generating direct merchants report", e);
//            ApiResponse<TransactionReportResponse> errorResponse = new ApiResponse<>();
//            errorResponse.setSuccess(false);
//            errorResponse.setMessage("Failed to generate direct merchants report: " + e.getMessage());
//            errorResponse.setTimestamp(LocalDateTime.now());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    /// new ones
    /**
     * Get enhanced merchant transaction type breakdown
     * GET /api/v1/reports/transactions/merchant/breakdown
     */
    @GetMapping("/merchant/breakdown")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEnhancedMerchantTransactionTypeBreakdown(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType) {

        logger.info("Received request for merchant transaction type breakdown: startDate={}, endDate={}, merchantId={}, dateFilter={}",
                startDate, endDate, merchantId, dateFilterType);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setMerchantId(merchantId);
            request.setDateFilterType(dateFilterType);

            List<Map<String, Object>> breakdown = transactionReportService.getEnhancedMerchantTransactionTypeBreakdown(request);

            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Merchant transaction type breakdown retrieved successfully");
            response.setData(breakdown);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting merchant transaction type breakdown", e);
            ApiResponse<List<Map<String, Object>>> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to get merchant transaction type breakdown: " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    /**
     * Get top merchants by commission for a franchise
     * GET /api/v1/reports/transactions/franchise/top-merchants
     */
    @GetMapping("/franchise/top-merchants")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('FRANCHISE')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEnhancedTopMerchantsByCommission(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "franchiseId", required = false) Long franchiseId,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType) {

        logger.info("Received request for top merchants by commission: startDate={}, endDate={}, franchiseId={}, dateFilter={}",
                startDate, endDate, franchiseId, dateFilterType);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setFranchiseId(franchiseId);
            request.setDateFilterType(dateFilterType);

            List<Map<String, Object>> topMerchants = transactionReportService.getEnhancedTopMerchantsByCommission(request);

            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("Top merchants by commission retrieved successfully");
            response.setData(topMerchants);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting top merchants by commission", e);
            ApiResponse<List<Map<String, Object>>> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to get top merchants by commission: " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/detailed")
//    @Operation(summary = "Get detailed transaction report",
//            description = "Retrieve comprehensive transaction details with filters")
    public ResponseEntity<Page<TransactionReportDTO.DetailedTransactionReportDTO>> getDetailedTransactionReport(
//            @Parameter(description = "Start date for the report (YYYY-MM-DDTHH:mm:ss)")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            //@Parameter(description = "End date for the report (YYYY-MM-DDTHH:mm:ss)")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            ///@Parameter(description = "Franchise ID filter (optional)")
            @RequestParam(required = false) Long franchiseId,

            //@Parameter(description = "Merchant ID filter (optional)")
            @RequestParam(required = false) Long merchantId,

            //@Parameter(description = "Transaction status filter (optional)")
            @RequestParam(required = false) String status,

            //@Parameter(description = "Card type filter (optional)")
            @RequestParam(required = false) String cardType,

            //@Parameter(description = "Brand type filter (optional)")
            @RequestParam(required = false) String brandType,

            //@Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            //@Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size,

            //@Parameter(description = "Sort field")
            @RequestParam(defaultValue = "txnDate") String sort,

            //@Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<TransactionReportDTO.DetailedTransactionReportDTO> report = transactionReportService
                .getDetailedTransactionReport(startDate, endDate, franchiseId, merchantId,
                        status, cardType, brandType, pageable);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/detailed/settlement-date")
//    @Operation(summary = "Get detailed transaction report by settlement date",
//            description = "Retrieve comprehensive transaction details filtered by settlement date")
    public ResponseEntity<Page<TransactionReportDTO.DetailedTransactionReportDTO>> getDetailedTransactionReportBySettlementDate(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) String brandType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "settleDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<TransactionReportDTO.DetailedTransactionReportDTO> report = transactionReportService
                .getDetailedTransactionReportBySettlementDate(startDate, endDate, franchiseId,
                        merchantId, status, cardType, brandType, pageable);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/summary/card-brand")
//    @Operation(summary = "Get card type and brand summary",
//            description = "Retrieve aggregated data by card type and brand")
    public ResponseEntity<List<TransactionReportDTO.CardTypeBrandSummaryDTO>> getCardTypeBrandSummary(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId) {

        List<TransactionReportDTO.CardTypeBrandSummaryDTO> summary = transactionReportService
                .getCardTypeBrandSummary(startDate, endDate, franchiseId);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/daily")
//    @Operation(summary = "Get daily transaction summary",
//            description = "Retrieve daily aggregated transaction data")
    public ResponseEntity<List<TransactionReportDTO.DailySummaryReportDTO>> getDailySummaryReport(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId) {

        List<TransactionReportDTO.DailySummaryReportDTO> summary = transactionReportService
                .getDailySummaryReport(startDate, endDate, franchiseId);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/performance/merchant")
//    @Operation(summary = "Get merchant performance report",
//            description = "Retrieve detailed performance metrics for merchants")
    public ResponseEntity<List<TransactionReportDTO.MerchantPerformanceDTO>> getMerchantWiseDetailedPerformance(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId) {

        List<TransactionReportDTO.MerchantPerformanceDTO> performance = transactionReportService
                .getMerchantWiseDetailedPerformance(startDate, endDate, franchiseId);

        return ResponseEntity.ok(performance);
    }

    @GetMapping("/comparison/franchise")
//    @Operation(summary = "Get franchise comparison report",
//            description = "Retrieve comparative performance data across franchises")
    public ResponseEntity<List<TransactionReportDTO.FranchiseComparisonDTO>> getFranchiseComparisonReport(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate) {

        List<TransactionReportDTO.FranchiseComparisonDTO> comparison = transactionReportService
                .getFranchiseComparisonReport(startDate, endDate);

        return ResponseEntity.ok(comparison);
    }

    @GetMapping("/analysis/terminal")
//    @Operation(summary = "Get terminal-wise transaction analysis",
//            description = "Retrieve transaction analysis grouped by terminals")
    public ResponseEntity<List<TransactionReportDTO.TerminalAnalysisDTO>> getTerminalWiseAnalysis(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId,
            @RequestParam(required = false) Long merchantId) {

        List<TransactionReportDTO.TerminalAnalysisDTO> analysis = transactionReportService
                .getTerminalWiseAnalysis(startDate, endDate, franchiseId, merchantId);

        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/trends/hourly")
//    @Operation(summary = "Get hourly transaction trends",
//            description = "Retrieve transaction trends broken down by hour")
    public ResponseEntity<List<TransactionReportDTO.HourlyTrendDTO>> getHourlyTransactionTrend(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId) {

        List<TransactionReportDTO.HourlyTrendDTO> trends = transactionReportService
                .getHourlyTransactionTrend(startDate, endDate, franchiseId);

        return ResponseEntity.ok(trends);
    }

    @GetMapping("/analysis/failed")
//    @Operation(summary = "Get failed transaction analysis",
//            description = "Retrieve detailed analysis of failed transactions")
    public ResponseEntity<Page<TransactionReportDTO.FailedTransactionDTO>> getFailedTransactionAnalysis(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<TransactionReportDTO.FailedTransactionDTO> analysis = transactionReportService
                .getFailedTransactionAnalysis(startDate, endDate, franchiseId, merchantId, pageable);

        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/analysis/settlement-delay")
//    @Operation(summary = "Get settlement delay analysis",
//            description = "Retrieve analysis of transaction settlement delays")
    public ResponseEntity<List<TransactionReportDTO.SettlementDelayAnalysisDTO>> getSettlementDelayAnalysis(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId) {

        List<TransactionReportDTO.SettlementDelayAnalysisDTO> analysis = transactionReportService
                .getSettlementDelayAnalysis(startDate, endDate, franchiseId);

        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/summary/overview")
//    @Operation(summary = "Get transaction summary overview",
//            description = "Retrieve high-level transaction statistics")
    public ResponseEntity<TransactionReportDTO.TransactionSummaryDTO> getTransactionSummary(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long franchiseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String transactionType) {

        TransactionReportDTO.TransactionSummaryDTO summary = transactionReportService
                .getTransactionSummary(startDate, endDate, franchiseId, status, transactionType);

        return ResponseEntity.ok(summary);
    }



    /// new ones with detailed for merchant  only 22-09
    /**
     * Get merchant-only transaction details report by transaction date
     * GET /api/v1/reports/transactions/merchant-only/details
     */
    @GetMapping("/merchant-only/details")
   // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<Page<DetailedTransactionReportDTO>> getMerchantOnlyTransactionDetails(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) String brandType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<DetailedTransactionReportDTO> report = transactionReportService
                .getMerchantOnlyTransactionDetails(startDate, endDate, merchantId,
                        status, cardType, brandType, pageable);

        return ResponseEntity.ok(report);
    }

    /**
     * Get merchant-only transaction details report by settlement date
     * GET /api/v1/reports/transactions/merchant-only/details/settlement-date
     */
    @GetMapping("/merchant-only/details/settlement-date")
   // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<Page<DetailedTransactionReportDTO>> getMerchantOnlyTransactionDetailsBySettlementDate(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) String brandType,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<DetailedTransactionReportDTO> report = transactionReportService
                .getMerchantOnlyTransactionDetailsBySettlementDate(startDate, endDate, merchantId,
                        status, cardType, brandType, pageable);

        return ResponseEntity.ok(report);
    }

    /**
     * Get merchant-only card type brand summary by transaction date
     * GET /api/v1/reports/transactions/merchant-only/card-brand-summary
     */
    @GetMapping("/merchant-only/card-brand-summary")
   // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<List<CardTypeBrandSummaryDTO>> getMerchantOnlyCardTypeBrandSummary(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId) {

        List<CardTypeBrandSummaryDTO> summary = transactionReportService
                .getMerchantOnlyCardTypeBrandSummary(startDate, endDate, merchantId);

        return ResponseEntity.ok(summary);
    }

    /**
     * Get merchant-only card type brand summary by settlement date
     * GET /api/v1/reports/transactions/merchant-only/card-brand-summary/settlement-date
     */
    @GetMapping("/merchant-only/card-brand-summary/settlement-date")
   // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<List<CardTypeBrandSummaryDTO>> getMerchantOnlyCardTypeBrandSummaryBySettlementDate(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId) {

        List<CardTypeBrandSummaryDTO> summary = transactionReportService
                .getMerchantOnlyCardTypeBrandSummaryBySettlementDate(startDate, endDate, merchantId);

        return ResponseEntity.ok(summary);
    }

    /**
     * Get merchant-only daily summary by transaction date
     * GET /api/v1/reports/transactions/merchant-only/daily-summary
     */
    @GetMapping("/merchant-only/daily-summary")
   // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<List<DailySummaryReportDTO>> getMerchantOnlyDailySummary(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId) {

        List<DailySummaryReportDTO> summary = transactionReportService
                .getMerchantOnlyDailySummary(startDate, endDate, merchantId);

        return ResponseEntity.ok(summary);
    }

    /**
     * Get merchant-only daily summary by settlement date
     * GET /api/v1/reports/transactions/merchant-only/daily-summary/settlement-date
     */
    @GetMapping("/merchant-only/daily-summary/settlement-date")
   // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<List<DailySummaryReportDTO>> getMerchantOnlyDailySummaryBySettlementDate(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId) {

        List<DailySummaryReportDTO> summary = transactionReportService
                .getMerchantOnlyDailySummaryBySettlementDate(startDate, endDate, merchantId);

        return ResponseEntity.ok(summary);
    }

    /**
     * Get merchant-only terminal analysis by transaction date
     * GET /api/v1/reports/transactions/merchant-only/terminal-analysis
     */
    @GetMapping("/merchant-only/terminal-analysis")
  //  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<List<TerminalAnalysisDTO>> getMerchantOnlyTerminalAnalysis(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId) {

        List<TerminalAnalysisDTO> analysis = transactionReportService
                .getMerchantOnlyTerminalAnalysis(startDate, endDate, merchantId);

        return ResponseEntity.ok(analysis);
    }

    /**
     * Get merchant-only terminal analysis by settlement date
     * GET /api/v1/reports/transactions/merchant-only/terminal-analysis/settlement-date
     */
    @GetMapping("/merchant-only/terminal-analysis/settlement-date")
   // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('MERCHANT')")
    public ResponseEntity<List<TransactionReportDTO.TerminalAnalysisDTO>> getMerchantOnlyTerminalAnalysisBySettlementDate(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Long merchantId) {

        List<TransactionReportDTO.TerminalAnalysisDTO> analysis = transactionReportService
                .getMerchantOnlyTerminalAnalysisBySettlementDate(startDate, endDate, merchantId);

        return ResponseEntity.ok(analysis);
    }



// excel export

    /**
     * Export all merchant transactions as Excel (optimized for large datasets)
     * GET /api/v1/reports/transactions/merchant/export-all
     */
    @GetMapping("/merchant/export-all")
    public ResponseEntity<Resource> exportAllMerchantTransactions(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType,
            @RequestParam(value = "merchantType", required = false) String merchantType, // NEW PARAM
            @RequestParam(value = "includeTaxes", defaultValue = "false") Boolean includeTaxes) {

        logger.info("Exporting all merchant transactions: startDate={}, endDate={}, dateFilter={}, merchantType={}, includeTaxes={}",
                startDate, endDate, dateFilterType, merchantType, includeTaxes);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setTransactionStatus(status);
            request.setTransactionType(transactionType);
            request.setDateFilterType(dateFilterType);
            request.setMerchantType(merchantType); // NEW: DIRECT, FRANCHISE, or null
            request.setMerchantId(null); // null means ALL merchants of selected type

            String userRole = getUserRoleFromSecurityContext();

            // Generate Excel file
            ByteArrayInputStream excelStream = transactionReportService
                    .exportAllMerchantTransactionsToExcel(request, includeTaxes, userRole,merchantType);

            InputStreamResource resource = new InputStreamResource(excelStream);

            String merchantTypeLabel = merchantType != null ? merchantType.toLowerCase() + "_" : "";
            String filename = String.format("merchant_transactions_%s%s_to_%s.xlsx",
                    merchantTypeLabel,
                    LocalDate.parse(startDate.substring(0, 10)),
                    LocalDate.parse(endDate.substring(0, 10)));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error exporting all merchant transactions", e);
            throw new BusinessException("Failed to export transactions: " + e.getMessage());
        }
    }

    /**
     * Export all franchise transactions as Excel (optimized for large datasets)
     * GET /api/v1/reports/transactions/franchise/export-all
     */
    @GetMapping("/franchise/export-all")
    public ResponseEntity<Resource> exportAllFranchiseTransactions(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "dateFilterType", defaultValue = "TRANSACTION_DATE") String dateFilterType,
            @RequestParam(value = "includeTaxes", defaultValue = "false") Boolean includeTaxes) {

        logger.info("Exporting all franchise transactions: startDate={}, endDate={}, dateFilter={}, includeTaxes={}",
                startDate, endDate, dateFilterType, includeTaxes);

        try {
            TransactionReportRequest request = new TransactionReportRequest();
            request.setStartDate(LocalDateTime.parse(startDate));
            request.setEndDate(LocalDateTime.parse(endDate));
            request.setTransactionStatus(status);
            request.setTransactionType(transactionType);
            request.setDateFilterType(dateFilterType);
            request.setFranchiseId(null); // null means ALL franchises

            String userRole = getUserRoleFromSecurityContext();

            // Generate Excel file
            ByteArrayInputStream excelStream = transactionReportService
                    .exportAllFranchiseTransactionsToExcel(request, includeTaxes, userRole);

            InputStreamResource resource = new InputStreamResource(excelStream);

            String filename = String.format("franchise_transactions_%s_to_%s.xlsx",
                    LocalDate.parse(startDate.substring(0, 10)),
                    LocalDate.parse(endDate.substring(0, 10)));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error exporting all franchise transactions", e);
            throw new BusinessException("Failed to export transactions: " + e.getMessage());
        }
    }

    private String getUserRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ADMIN");
        }
        return "ADMIN";
    }



}