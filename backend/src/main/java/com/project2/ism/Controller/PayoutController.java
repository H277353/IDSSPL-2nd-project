package com.project2.ism.Controller;




import com.project2.ism.DTO.PayoutDTO.*;
import com.project2.ism.Model.Payout.PayoutTransaction;
import com.project2.ism.Repository.PayoutTransactionRepository;
import com.project2.ism.Service.PayoutService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for payout operations
 */
@RestController
@RequestMapping("/payment-payout")
public class PayoutController {

    private static final Logger log = LoggerFactory.getLogger(PayoutController.class);

    @Value("${payout.default.vendor.id}")
    private Long defaultPayoutVendorId; // ViMoPay - TODO: Make dynamic when multiple vendors available

    private final PayoutService payoutService;
    private final PayoutTransactionRepository payoutTxnRepo;

    public PayoutController(PayoutService payoutService,
                            PayoutTransactionRepository payoutTxnRepo) {
        this.payoutService = payoutService;
        this.payoutTxnRepo = payoutTxnRepo;
    }

    /**
     * Initiate a payout
     * POST /api/payment-payout/initiate
     */
    @PostMapping("/initiate")
    public ResponseEntity<PayoutResult> initiatePayout(
            @Valid @RequestBody PayoutRequest request) {

        log.info("Payout initiation request: initiator={} type={} amount={} ref={}",
                request.getInitiatorId(), request.getInitiatorType(),
                request.getAmount(), request.getMerchantRefId());

        PayoutResult result = payoutService.initiatePayout(request, defaultPayoutVendorId);

        if ("FAILED".equals(result.getStatus())) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * NEW: Simplified payout using saved bank account
     * POST /api/payment-payout/transfer
     *
     * Request body:
     * {
     *   "payoutBankId": 5,
     *   "amount": 1000.00,
     *   "paymentMode": "NEFT",

     * }
     */
    @PostMapping("/transfer")
    public ResponseEntity<SimplePayoutResponse> transferToSavedBank(
            @Valid @RequestBody SimplePayoutRequest request) {

        log.info("Simple payout request: bankId={} amount={} mode={} ",
                request.getPayoutBankId(), request.getAmount(),
                request.getPaymentMode());

        PayoutResult result = payoutService.initiateSimplePayout(request, defaultPayoutVendorId);

        SimplePayoutResponse response = new SimplePayoutResponse(
                result.getStatus(), result.getMessage(), result.getTxnId(), result.getMerchantRefId()
        );
        if ("FAILED".equals(result.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<Object> receiveVendorCallback(@RequestBody Map<String, Object> body) {

        try {
            payoutService.handleEncryptedCallback(body);
            return ResponseEntity.ok(
                    Map.of(
                            "successStatus", true,
                            "message", "Success",
                            "responseCode", "000"
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "successStatus", false,
                            "message", e.getMessage(),
                            "responseCode", "500"
                    ));
        }
    }

    /**
     * Get payout transaction by merchant reference
     * GET /api/payment-payout/status?merchantRef=REF123
     */
    @GetMapping("/status")
    public ResponseEntity<PayoutTransaction> getPayoutStatus(
            @RequestParam String merchantRef) {

        return payoutTxnRepo.findByMerchantRefId(merchantRef)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get payout history for merchant/franchise
     * GET /api/payment-payout/history?initiatorType=MERCHANT&initiatorId=123
     */
    @GetMapping("/history")
    public ResponseEntity<List<PayoutTransaction>> getPayoutHistory(
            @RequestParam String initiatorType,
            @RequestParam Long initiatorId,
            @RequestParam(required = false) String status) {

        List<PayoutTransaction> transactions;

        if (status != null) {
            PayoutTransaction.PayoutStatus payoutStatus = PayoutTransaction.PayoutStatus.valueOf(status);
            transactions = payoutTxnRepo.findByInitiatorTypeAndInitiatorIdAndStatusOrderByCreatedAtDesc(
                    initiatorType, initiatorId, payoutStatus);
        } else {
            transactions = payoutTxnRepo.findByInitiatorTypeAndInitiatorIdOrderByCreatedAtDesc(
                    initiatorType, initiatorId);
        }

        return ResponseEntity.ok(transactions);
    }

    /**
     * Get payout statistics
     * GET /api/payment-payout/stats?initiatorType=MERCHANT&initiatorId=123
     */
    @GetMapping("/stats")
    public ResponseEntity<PayoutStats> getPayoutStats(
            @RequestParam String initiatorType,
            @RequestParam Long initiatorId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to) {

        LocalDateTime startDate = from != null ? from : LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = to != null ? to : LocalDateTime.now();

        List<PayoutTransaction> transactions = payoutTxnRepo.findByInitiatorAndDateRange(
                initiatorType, initiatorId, startDate, endDate);

        PayoutStats stats = new PayoutStats(transactions);
        return ResponseEntity.ok(stats);
    }


}
