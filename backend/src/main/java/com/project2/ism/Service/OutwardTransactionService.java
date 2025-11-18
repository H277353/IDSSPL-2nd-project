package com.project2.ism.Service;

import com.project2.ism.DTO.FranchiseInwardDTO;
import com.project2.ism.DTO.MerchantInwardDTO;
import com.project2.ism.DTO.OutwardTransactionDTO;
import com.project2.ism.DTO.ProductSerialDTO;
import com.project2.ism.Exception.DuplicateResourceException;
import com.project2.ism.Exception.ResourceNotFoundException;
import com.project2.ism.Model.InventoryTransactions.OutwardTransactions;
import com.project2.ism.Model.InventoryTransactions.ProductSerialNumbers;
import com.project2.ism.Model.Product;
import com.project2.ism.Model.Users.Franchise;
import com.project2.ism.Model.Users.Merchant;
import com.project2.ism.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutwardTransactionService {

        private final OutwardTransactionRepository outwardTransactionRepository;
        private final FranchiseRepository franchiseRepo;
        private final MerchantRepository merchantRepo;
        private final ProductRepository productRepo;
        private final ProductSerialsRepository serialRepo;

        public OutwardTransactionService(
                OutwardTransactionRepository outwardTransactionRepository,
                FranchiseRepository franchiseRepo,
                MerchantRepository merchantRepo,
                ProductRepository productRepo,
                ProductSerialsRepository serialRepo) {
            this.outwardTransactionRepository = outwardTransactionRepository;
            this.franchiseRepo = franchiseRepo;
            this.merchantRepo = merchantRepo;
            this.productRepo = productRepo;
            this.serialRepo = serialRepo;
        }

        public List<OutwardTransactions> getAll() {
            return outwardTransactionRepository.findAll();
        }

        public OutwardTransactions getById(Long id) {
            return outwardTransactionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Outward Transaction not found with id " + id));
        }

        @Transactional
        public OutwardTransactions
        createFromDTO(OutwardTransactionDTO dto) {
//            if (outwardTransactionRepository.existsByDeliveryNumber(dto.deliveryNumber)) {
//                throw new DuplicateResourceException("Delivery number already exists: " + dto.deliveryNumber);
//            }



            Franchise franchise = dto.franchiseId != null
                    ? franchiseRepo.findById(dto.franchiseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Franchise not found"))
                    : null;

            Merchant merchant = dto.merchantId != null
                    ? merchantRepo.findById(dto.merchantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"))
                    : null;

            Product product = dto.productId != null
                    ? productRepo.findById(dto.productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"))
                    : null;

            dto.deliveryNumber = generateDeliveryNumber();
            OutwardTransactions outward = dto.toEntity(franchise, merchant, product,serialRepo);
//            outward.setDeliveryNumber(generateDeliveryNumber());
            return outwardTransactionRepository.save(outward);
        }


        // 👇 Helper method should be private and inside this service
        public String generateDeliveryNumber() {
            String prefix = "D";
            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            long countToday = outwardTransactionRepository.countByDispatchDate(LocalDate.now()) + 1;
            String sequence = String.format("%03d", countToday);
            return prefix + "-" + datePart + "-" + sequence;
        }


        @Transactional
        public OutwardTransactions updateFromDTO(Long id, OutwardTransactionDTO dto) {
            OutwardTransactions existing = getById(id);

            Franchise franchise = dto.franchiseId != null
                    ? franchiseRepo.findById(dto.franchiseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Franchise not found"))
                    : null;

            Merchant merchant = dto.merchantId != null
                    ? merchantRepo.findById(dto.merchantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"))
                    : null;

            Product product = dto.productId != null
                    ? productRepo.findById(dto.productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"))
                    : null;

            // map DTO → existing entity
            existing.setDeliveryNumber(dto.deliveryNumber);
            existing.setFranchise(franchise);
            existing.setMerchant(merchant);
            existing.setProduct(product);
            existing.setDispatchDate(dto.dispatchDate);
            existing.setDispatchedBy(dto.dispatchedBy);
            existing.setQuantity(dto.quantity);
            existing.setDeliveryAddress(dto.deliveryAddress);
            existing.setContactPerson(dto.contactPerson);
            existing.setContactPersonNumber(dto.contactPersonNumber);
            existing.setDeliveryMethod(dto.deliveryMethod);
            existing.setTrackingNumber(dto.trackingNumber);
            existing.setExpectedDeliveryDate(dto.expectedDelivery);
            existing.setRemarks(dto.remarks);

            if (dto.serialNumbers != null) {
                existing.setProductSerialNumbers(
                        dto.serialNumbers.stream()
                                .map(sn -> sn.toOutwardEntity(existing, serialRepo))
                                .collect(Collectors.toList())
                );
            }

            return outwardTransactionRepository.save(existing);
        }

        @Transactional
        public void delete(Long id) {
            OutwardTransactions existing = getById(id);
            // Break relationship manually
            for (ProductSerialNumbers psn : existing.getProductSerialNumbers()) {
                psn.setOutwardTransaction(null);
            }
            outwardTransactionRepository.delete(existing);
        }


    @Transactional
    public void receivedDateService(Long outwardId) {
        OutwardTransactions outward = outwardTransactionRepository.findById(outwardId)
                .orElseThrow(() -> new IllegalArgumentException("Outward transaction not found: " + outwardId));

        LocalDateTime now = LocalDateTime.now();

        // Set received date for outward
        outward.setReceivedDate(now);
        outwardTransactionRepository.save(outward);

        // Update all associated ProductSerialNumbers
        if (outward.getProductSerialNumbers() != null && !outward.getProductSerialNumbers().isEmpty()) {
            for (ProductSerialNumbers psn : outward.getProductSerialNumbers()) {
                psn.setReceivedDateByFranchise(now); // make sure this field exists in PSN entity
            }
            serialRepo.saveAll(outward.getProductSerialNumbers());
        }
    }



    public List<FranchiseInwardDTO> getFranchiseInward(Long franchiseId) {
        List<OutwardTransactions> list = outwardTransactionRepository.findByFranchiseIdWithSerials(franchiseId);

        return list.stream().map(outward -> {
            FranchiseInwardDTO dto = new FranchiseInwardDTO();
            dto.setOutwardId(outward.getId());
            dto.setProductName(outward.getProduct().getProductName()); // assuming relation exists
            dto.setQuantity(Long.valueOf(outward.getQuantity()));
            dto.setDeliveryMethod(outward.getDeliveryMethod());
            dto.setTrackingNumber(outward.getTrackingNumber());
            dto.setDispatchDate(outward.getDispatchDate());
            dto.setExpectedDeliveryDate(outward.getExpectedDeliveryDate());
            dto.setReceivedDate(outward.getReceivedDate());
            List<ProductSerialDTO> psnList = outward.getProductSerialNumbers()
                    .stream()
                    .map(psn -> {
                        ProductSerialDTO p = new ProductSerialDTO();
                        p.setId(psn.getId());
                        p.setSid(psn.getSid());
                        p.setMid(psn.getMid());
                        p.setTid(psn.getTid());
                        p.setVpaid(psn.getVpaid());
                        p.setMobNumber(psn.getMobNumber());
                        return p;
                    })
                    .toList();

            dto.setProductSerialNumbers(psnList);

            return dto;
        }).toList();
    }

    public List<MerchantInwardDTO> getMerchantInward(Long merchantId) {
        List<OutwardTransactions> list = outwardTransactionRepository.findByMerchantId(merchantId);

        return list.stream().map(outward -> {
            MerchantInwardDTO dto = new MerchantInwardDTO();
            dto.setOutwardId(outward.getId());
            dto.setProductName(outward.getProduct().getProductName()); // assuming relation exists
            dto.setQuantity(Long.valueOf(outward.getQuantity()));
            dto.setDeliveryMethod(outward.getDeliveryMethod());
            dto.setTrackingNumber(outward.getTrackingNumber());
            dto.setDispatchDate(outward.getDispatchDate());
            dto.setExpectedDeliveryDate(outward.getExpectedDeliveryDate());
            dto.setReceivedDate(outward.getReceivedDate());
            List<ProductSerialDTO> psnList = outward.getProductSerialNumbers()
                    .stream()
                    .map(psn -> {
                        ProductSerialDTO p = new ProductSerialDTO();
                        p.setId(psn.getId());
                        p.setSid(psn.getSid());
                        p.setMid(psn.getMid());
                        p.setTid(psn.getTid());
                        p.setVpaid(psn.getVpaid());
                        p.setMobNumber(psn.getMobNumber());
                        return p;
                    })
                    .toList();

            dto.setProductSerialNumbers(psnList);
            return dto;
        }).toList();
    }



    //only for updating the database one time
    @Transactional
    public int backfillReceivedDateByFranchise(Long franchiseId) {
        // Get all outward transactions for this franchise that have a receivedDate
        List<OutwardTransactions> outwards = outwardTransactionRepository.findByFranchiseIdAndReceivedDateIsNotNull(franchiseId);

        int updatedCount = 0;

        for (OutwardTransactions outward : outwards) {
            LocalDateTime receivedDate = outward.getReceivedDate();
            if (receivedDate == null || outward.getProductSerialNumbers() == null) continue;

            for (ProductSerialNumbers psn : outward.getProductSerialNumbers()) {
                // Only update if null to avoid overwriting
                if (psn.getReceivedDateByFranchise() == null) {
                    psn.setReceivedDateByFranchise(receivedDate);
                }
                if (psn.getFranchise() == null) {
                    psn.setFranchise(outward.getFranchise());
                }
            }

            serialRepo.saveAll(outward.getProductSerialNumbers());
            updatedCount += outward.getProductSerialNumbers().size();
        }

        return updatedCount;
    }


}

