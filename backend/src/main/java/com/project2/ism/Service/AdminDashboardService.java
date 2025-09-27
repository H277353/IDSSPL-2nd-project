package com.project2.ism.Service;

import com.project2.ism.DTO.FranchiseMerchantStatsDTO;
import com.project2.ism.DTO.InventoryTransactionStatsDTO;
import com.project2.ism.DTO.VendorStatsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
public class AdminDashboardService {

    private final FranchiseService franchiseService;
    private final PricingSchemeService pricingSchemeService;
    private final VendorService vendorService;
    private final StatsService statsService;
    private final ProductService productService;

    public AdminDashboardService(FranchiseService franchiseService,
                                 PricingSchemeService pricingSchemeService,
                                 VendorService vendorService,
                                 StatsService statsService,
                                 ProductService productService) {
        this.franchiseService = franchiseService;
        this.pricingSchemeService = pricingSchemeService;
        this.vendorService = vendorService;
        this.statsService = statsService;
        this.productService = productService;
    }

    /**
     * Get all dashboard statistics in a single consolidated response
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllDashboardStats() {
        Map<String, Object> dashboardStats = new HashMap<>();

        // Franchise and Merchant Stats
        FranchiseMerchantStatsDTO franchiseMerchantStats = franchiseService.getStats();
        dashboardStats.put("franchiseStats", franchiseMerchantStats);

        // Transaction Stats
        InventoryTransactionStatsDTO transactionStats = statsService.getTransactionStats();
        dashboardStats.put("transactionStats", transactionStats);

        // Pricing Scheme Stats
        Map<String, Object> pricingStats = new HashMap<>();
        pricingStats.put("totalSchemes", pricingSchemeService.getTotalSchemesCount());
        // Usage
        Map<String, Long> counts = pricingSchemeService.getSchemeCountsByCustomerType();
        pricingStats.put("totalFranchiseSchemes", counts.getOrDefault("franchise", 0L));
        pricingStats.put("totalDirectMerchantSchemes", counts.getOrDefault("direct_merchant", 0L));
        dashboardStats.put("pricingSchemeStats", pricingStats);

        // Vendor Stats
        VendorStatsDTO vendorStats = vendorService.getVendorStats();
        dashboardStats.put("vendorStats", vendorStats);

        // Product Stats
        Map<String, Object> productStats = productService.getProductStats();
        dashboardStats.put("productStats", productStats);

        return dashboardStats;
    }

//    // Keep individual methods if needed for specific use cases
//    public FranchiseMerchantStatsDTO getStats() {
//        FranchiseMerchantStatsDTO dto = new FranchiseMerchantStatsDTO();
//
//        dto.totalFranchises = franchiseRepository.count();
//        dto.totalMerchants = merchantRepository.count();
//        dto.totalDirectMerchants = merchantRepository.countDirectMerchants();
//        dto.totalFranchiseMerchants = merchantRepository.countFranchiseMerchants();
//
//        dto.totalFranchiseWalletBalance = franchiseRepository.sumWalletBalances();
//        dto.totalDirectMerchantWalletBalance = merchantRepository.sumDirectMerchantWallets();
//        dto.totalFranchiseMerchantWalletBalance = merchantRepository.sumFranchiseMerchantWallets();
//
//        dto.merchantsPerFranchise = merchantRepository.countByFranchise().stream()
//                .collect(Collectors.toMap(
//                        row -> (String) row[0],   // franchise name
//                        row -> (Long) row[1]      // merchant count
//                ));
//
//        return dto;
//    }
//
//    public InventoryTransactionStatsDTO getTransactionStats() {
//        InventoryTransactionStatsDTO dto = new InventoryTransactionStatsDTO();
//
//        // basic counts
//        dto.totalInwardTransactions = inwardTransactionRepository.count();
//        dto.totalOutwardTransactions = outwardTransactionRepository.count();
//        //dto.totalReturnTransactions = returnRepo.count();
//        dto.totalProductSerials = productSerialsRepository.count();
//
//        // inward grouped by vendor
//        dto.inwardByVendor = inwardTransactionRepository.countGroupByVendor();
//
//        // outward grouped by customer type
//        dto.outwardByCustomer = outwardTransactionRepository.countByCustomerType();
//
//        // return reasons distribution
//        //dto.returnReasons = returnRepo.countGroupByReason();
//
//        // serial number status
//        dto.productSerialStatus = productSerialsRepository.countByStatus();
//
//        return dto;
//    }
//
//    @Transactional(readOnly = true)
//    public long getTotalSchemesCount() {
//        return pricingSchemeRepository.count();
//    }
//
//    @Transactional(readOnly = true)
//    public List<PricingScheme> getAllSchemesForCustomerType(String customerType) {
//        return pricingSchemeRepository.findAll().stream()
//                .filter(scheme -> scheme.getCustomerType().equals(customerType))
//                .toList();
//    }
//
//    public VendorStatsDTO getVendorStats() {
//        VendorStatsDTO dto = new VendorStatsDTO();
//
//        // Vendors
//        dto.totalVendors = vendorRepository.count();
//        dto.activeVendors = vendorRepository.countByStatus(true);
//        dto.inactiveVendors = vendorRepository.countByStatus(false);
//
//        // Vendor Rates
//        dto.totalVendorRates = vendorRatesRepository.count();
//        LocalDate today = LocalDate.now();
//        dto.activeVendorRates = vendorRatesRepository.countByEffectiveDateBeforeAndExpiryDateAfter(today, today);
//
//        // Total Monthly Rent
//        dto.totalMonthlyRent = vendorRatesRepository.sumActiveMonthlyRent(today, today);
//
//        // Card Type Distribution
//        dto.cardTypeDistribution = vendorRatesRepository.countGroupByCardType();
//
//        return dto;
//    }
//
//    @Transactional(readOnly = true)
//    public Map<String, Object> getProductStats() {
//        List<ProductCategory> categories = getAllCategories();
//        long totalProducts = productRepository.count();
//        long activeProducts = productRepository.countByStatus(true);
//
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("totalProducts", totalProducts);
//        stats.put("activeProducts", activeProducts);
//        stats.put("inactiveProducts", totalProducts - activeProducts);
//        stats.put("totalCategories", categories.size());
//        stats.put("categoryBreakdown", categories);
//
//        return stats;
//    }
}