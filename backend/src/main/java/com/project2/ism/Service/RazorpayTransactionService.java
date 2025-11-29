package com.project2.ism.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project2.ism.DTO.RazorPay.RazorpayNotificationDTO;
import com.project2.ism.Model.VendorTransactions;
import com.project2.ism.Repository.VendorTransactionsRepository;
import com.project2.ism.Service.InstantSettlement.InstantSettlementTrigger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RazorpayTransactionService {

    private final VendorTransactionsRepository vendorTransactionsRepository;
    private final ObjectMapper mapper ;
    private final RazorpayNotificationLogService razorpayNotificationLogService;
    private final InstantSettlementTrigger instantSettlementTrigger; // NEW

    public RazorpayTransactionService(VendorTransactionsRepository vendorTransactionsRepository, ObjectMapper mapper, RazorpayNotificationLogService razorpayNotificationLogService, InstantSettlementTrigger instantSettlementTrigger) {
        this.vendorTransactionsRepository = vendorTransactionsRepository;
        this.mapper = mapper;
        this.razorpayNotificationLogService = razorpayNotificationLogService;
        this.instantSettlementTrigger = instantSettlementTrigger;
    }

    public void process(String rawJson) {

        long start = System.currentTimeMillis();
        Long logId = null;
        String txnId = null;

        try {
            // Step 1: Parse DTO
            RazorpayNotificationDTO dto =
                    mapper.readValue(rawJson, RazorpayNotificationDTO.class);

            txnId = dto.getTxnId();   // txnId extracted

            // Step 2: create initial log entry (RECEIVED)
            logId = razorpayNotificationLogService.logReceived(rawJson, txnId);

            // Step 3: find existing transaction
            VendorTransactions existing =
                    vendorTransactionsRepository.findByTransactionReferenceId(dto.getTxnId())
                            .orElse(null);

            VendorTransactions tx =
                    (existing != null) ? existing : new VendorTransactions();

            // Step 4: map fields
            applyMapping(dto, tx);

            // Step 5: save final transaction
            vendorTransactionsRepository.save(tx);

            // Step 6: success log
            long time = System.currentTimeMillis() - start;
            razorpayNotificationLogService.logSuccess(logId, time);

            //  NEW: Step 7: Check and trigger instant settlement if enabled
            instantSettlementTrigger.checkAndTrigger(tx.getTransactionReferenceId());


        } catch (Exception ex) {

            long time = System.currentTimeMillis() - start;

            // If log entry already created, update it
            if (logId != null) {
                razorpayNotificationLogService.logFailure(logId, ex.getMessage(), time);
            } else {
                // If log entry was NOT created due to parsing error,
                // create a failure log NOW with txnId = null
                razorpayNotificationLogService.logFailureNoDTO(rawJson, txnId, ex.getMessage(), time);
            }

            // Re-throw for controller to log error but NOT affect Razorpay response.
            throw new RuntimeException("Failed to process Razorpay notification: " + ex.getMessage(), ex);
        }
    }


    private void applyMapping(RazorpayNotificationDTO dto, VendorTransactions tx) {

        tx.setTransactionReferenceId(dto.getTxnId());
        tx.setUsername(dto.getUsername());
        tx.setMobile(dto.getUserMobile());
        tx.setConsumer(dto.getCustomerName());
        tx.setPayer(dto.getPayerName());

        tx.setAmount(big(dto.getAmount()));
        tx.setTip(big(dto.getAmountAdditional()));
        tx.setCashAtPos(big(dto.getAmountCashBack()));
        tx.setAmountOriginal(big(dto.getAmountOriginal()));

        tx.setTxnType(dto.getTxnType());
        tx.setType(dto.getTxnType());
        tx.setMode(dto.getPaymentMode());

        tx.setAuthCode(dto.getAuthCode());
        tx.setRrn(dto.getRrNumber());
        tx.setBatchNumber(dto.getBatchNumber());
        tx.setEmail(dto.getCustomerEmail());
        tx.setCard(dto.getFormattedPan());
        tx.setCardLastFourDigit(dto.getCardLastFourDigit());
        tx.setPaymentCardBin(dto.getPaymentCardBin());
        tx.setBrandType(dto.getPaymentCardBrand());
        tx.setCardType(dto.getPaymentCardType());

        // Card internal metadata
        tx.setCardClassification(dto.getCardClassification());
        tx.setCardTxnType(dto.getCardTxnType());
        //tx.setCardTxnTypeDesc(dto.getCardTxnTypeDesc());

        tx.setMerchant(dto.getMerchantName());
        tx.setOrgCode(dto.getOrgCode());
        tx.setMerchantCode(dto.getMerchantCode());

        // Razorpay reference numbers
        tx.setRef(dto.getExternalRefNumber());
        tx.setRef1(dto.getExternalRefNumber2());
        tx.setRef2(dto.getExternalRefNumber3());
        tx.setRef3(dto.getExternalRefNumber4());
        tx.setRef4(dto.getExternalRefNumber5());
        tx.setRef5(dto.getExternalRefNumber6());
        tx.setRef6(dto.getExternalRefNumber7());

        tx.setMid(dto.getMid());
        tx.setTid(dto.getTid());
        tx.setDeviceSerial(dto.getDeviceSerial());

        tx.setIssuingBank(dto.getBankName());

        tx.setStatus(dto.getStatus());
        tx.setSettlementStatus(dto.getSettlementStatus());

        tx.setOriginalTransactionId(dto.getReferenceTransactionId());
        tx.setReferenceTransactionId(dto.getReferenceTransactionId());

        tx.setPaymentGateway(dto.getPaymentGateway());

        // Date — convert Razorpay ISO string → LocalDateTime
        tx.setDate(parseDate(dto.getChargeSlipDate()));
    }

    private BigDecimal big(Double val) {
        return val == null ? BigDecimal.ZERO : BigDecimal.valueOf(val);
    }

    private LocalDateTime parseDate(String iso) {
        try {
            return OffsetDateTime.parse(iso, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toLocalDateTime();
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }

}
