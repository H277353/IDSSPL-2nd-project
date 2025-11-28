package com.project2.ism.Service;

import com.project2.ism.DTO.Vendor.VendorCredentialsRequestDTO;
import com.project2.ism.DTO.Vendor.VendorCredentialsResponseDTO;
import com.project2.ism.Model.Payment.PaymentVendor;
import com.project2.ism.Model.Payment.PaymentVendorCredentials;
import com.project2.ism.Model.Payment.PaymentVendorLog;
import com.project2.ism.Repository.PaymentProductRepository;
import com.project2.ism.Repository.PaymentVendorCredentialsRepository;
import com.project2.ism.Repository.PaymentVendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentVendorCredentialsService {


    private final PaymentVendorCredentialsRepository paymentVendorCredentialsRepository;
    private final PaymentVendorRepository paymentVendorRepository;
    private final PaymentProductRepository paymentProductRepository;

    public PaymentVendorCredentialsService(PaymentVendorCredentialsRepository paymentVendorCredentialsRepository, PaymentVendorRepository paymentVendorRepository, PaymentProductRepository paymentProductRepository) {
        this.paymentVendorCredentialsRepository = paymentVendorCredentialsRepository;
        this.paymentVendorRepository = paymentVendorRepository;
        this.paymentProductRepository = paymentProductRepository;
    }

    // Create
    public VendorCredentialsResponseDTO create(VendorCredentialsRequestDTO dto) {

        PaymentVendorCredentials vc = new PaymentVendorCredentials();

        vc.setPaymentVendor(paymentVendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found")));

        if (dto.getProductId() != null) {
            vc.setPaymentProduct(paymentProductRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found")));
        }

        mapDtoToEntity(dto, vc);
        paymentVendorCredentialsRepository.save(vc);

        return toResponseDTO(vc);
    }


    // Update
    public VendorCredentialsResponseDTO update(Long id, VendorCredentialsRequestDTO dto) {

        PaymentVendorCredentials vc = paymentVendorCredentialsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credentials not found"));

        mapDtoToEntity(dto, vc);
        paymentVendorCredentialsRepository.save(vc);

        return toResponseDTO(vc);
    }


    // Get One
    public VendorCredentialsResponseDTO getOne(Long id) {
        PaymentVendorCredentials vc = paymentVendorCredentialsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credentials not found"));
        return toResponseDTO(vc);
    }


    // Get All
    public List<VendorCredentialsResponseDTO> getAll() {
        return paymentVendorCredentialsRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }


    // Delete
    public void delete(Long id) {
        paymentVendorCredentialsRepository.deleteById(id);
    }


    // ------------------------------
    // Helpers
    // ------------------------------

    private void mapDtoToEntity(VendorCredentialsRequestDTO dto, PaymentVendorCredentials vc) {

        vc.setBaseUrlUat(dto.getBaseUrlUat());
        vc.setSecretKeyUat(dto.getSecretKeyUat());
        vc.setSaltKeyUat(dto.getSaltKeyUat());
        vc.setEncryptDecryptKeyUat(dto.getEncryptDecryptKeyUat());
        vc.setUserIdUat(dto.getUserIdUat());

        vc.setBaseUrlProd(dto.getBaseUrlProd());
        vc.setSecretKeyProd(dto.getSecretKeyProd());
        vc.setSaltKeyProd(dto.getSaltKeyProd());
        vc.setEncryptDecryptKeyProd(dto.getEncryptDecryptKeyProd());
        vc.setUserIdProd(dto.getUserIdProd());

        vc.setActiveEnvironment(dto.getActiveEnvironment());
        vc.setIsActive(dto.getIsActive());
    }


    @Transactional(readOnly = true)
    private VendorCredentialsResponseDTO toResponseDTO(PaymentVendorCredentials vc) {

        VendorCredentialsResponseDTO dto = new VendorCredentialsResponseDTO();

        dto.setId(vc.getId());

        // Vendor
        dto.setVendorId(vc.getPaymentVendor().getId());
        dto.setVendorName(vc.getPaymentVendor().getVendorName());

        // Product (nullable)
        if (vc.getPaymentProduct() != null) {
            dto.setProductId(vc.getPaymentProduct().getId());
            dto.setProductName(vc.getPaymentProduct().getProductName());
        } else {
            dto.setProductId(null);
            dto.setProductName(null);
        }

        dto.setBaseUrlUat(vc.getBaseUrlUat());
        dto.setBaseUrlProd(vc.getBaseUrlProd());

        dto.setActiveEnvironment(vc.getActiveEnvironment());
        dto.setIsActive(vc.getIsActive());

        dto.setCreatedOn(vc.getCreatedAt());
        dto.setUpdatedOn(vc.getUpdatedAt());

        return dto;
    }


    // Fetch the credential row for this vendor
    public PaymentVendorCredentials getCredentials(Long vendorId) {
        return paymentVendorCredentialsRepository
                .findByPaymentVendorIdAndIsActive(vendorId, true)
                .orElseThrow(() -> new RuntimeException("Active vendor credentials not found for vendorId = " + vendorId));
    }


    // Core function: returns UAT or PROD creds based on activeEnvironment
    public PaymentVendorCredentials getActiveEnvironmentCredentials(Long vendorId) {
        PaymentVendorCredentials creds = getCredentials(vendorId);

        String env = creds.getActiveEnvironment();
        if (env == null) {
            throw new RuntimeException("Active environment not set for vendorId = " + vendorId);
        }

        if (env.equalsIgnoreCase("UAT")) {
            validateUAT(creds);
        } else if (env.equalsIgnoreCase("PROD")) {
            validatePROD(creds);
        } else {
            throw new RuntimeException("Invalid active environment value: " + env);
        }

        return creds;
    }


    // ---------------------------------------------------------
    //  Environment-specific credential accessors
    // ---------------------------------------------------------

    public String getBaseUrl(Long vendorId) {
        PaymentVendorCredentials c = getActiveEnvironmentCredentials(vendorId);
        return c.getActiveEnvironment().equalsIgnoreCase("UAT") ?
                c.getBaseUrlUat() : c.getBaseUrlProd();
    }

    public String getSecretKey(Long vendorId) {
        PaymentVendorCredentials c = getActiveEnvironmentCredentials(vendorId);
        return c.getActiveEnvironment().equalsIgnoreCase("UAT") ?
                c.getSecretKeyUat() : c.getSecretKeyProd();
    }

    public String getSaltKey(Long vendorId) {
        PaymentVendorCredentials c = getActiveEnvironmentCredentials(vendorId);
        return c.getActiveEnvironment().equalsIgnoreCase("UAT") ?
                c.getSaltKeyUat() : c.getSaltKeyProd();
    }

    public String getEncryptDecryptKey(Long vendorId) {
        PaymentVendorCredentials c = getActiveEnvironmentCredentials(vendorId);
        return c.getActiveEnvironment().equalsIgnoreCase("UAT") ?
                c.getEncryptDecryptKeyUat() : c.getEncryptDecryptKeyProd();
    }

    public String getUserId(Long vendorId) {
        PaymentVendorCredentials c = getActiveEnvironmentCredentials(vendorId);
        return c.getActiveEnvironment().equalsIgnoreCase("UAT") ?
                c.getUserIdUat() : c.getUserIdProd();
    }


    // ---------------------------------------------------------
    //  Validation helpers: fail early if missing creds
    // ---------------------------------------------------------

    private void validateUAT(PaymentVendorCredentials c) {
        if (c.getBaseUrlUat() == null || c.getSecretKeyUat() == null ||
                c.getSaltKeyUat() == null || c.getEncryptDecryptKeyUat() == null ||
                c.getUserIdUat() == null) {
            throw new RuntimeException("Missing UAT credentials for vendorId = " + c.getPaymentVendor().getId());
        }
    }

    private void validatePROD(PaymentVendorCredentials c) {
        if (c.getBaseUrlProd() == null || c.getSecretKeyProd() == null ||
                c.getSaltKeyProd() == null || c.getEncryptDecryptKeyProd() == null ||
                c.getUserIdProd() == null) {
            throw new RuntimeException("Missing PROD credentials for vendorId = " + c.getPaymentVendor().getId());
        }
    }
}

