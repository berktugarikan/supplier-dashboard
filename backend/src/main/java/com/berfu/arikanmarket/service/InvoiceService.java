package com.berfu.arikanmarket.service;


import com.berfu.arikanmarket.entity.Invoice;
import com.berfu.arikanmarket.entity.Supplier;
import com.berfu.arikanmarket.repository.InvoiceRepository;
import com.berfu.arikanmarket.repository.SupplierRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SupplierRepository supplierRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, SupplierRepository supplierRepository) {
        this.invoiceRepository = invoiceRepository;
        this.supplierRepository = supplierRepository;
    }

    // Tüm faturaları getirir
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Belirli bir ID'ye sahip faturayı getirir
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    // Fatura ekler veya günceller
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    // Fatura siler
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    // Ödenmemiş faturaları getirir
    public List<Invoice> getUnpaidInvoices() {
        return invoiceRepository.findByPaymentStatusFalse();
    }

    // Ödenmiş faturaları getirir
    public List<Invoice> getPaidInvoices() {
        return invoiceRepository.findByPaymentStatusTrue();
    }

    // Belirli bir tedarikçiye göre ödenmemiş faturaları getirir
    public List<Invoice> getUnpaidInvoicesBySupplier(String supplierName) {
        Supplier supplier = supplierRepository.findByName(supplierName);
        if (supplier != null) {
            return invoiceRepository.findBySupplierAndPaymentStatusFalse(supplier);
        }
        return List.of(); // Tedarikçi bulunamazsa boş liste döner
    }

    // Belirli bir tarih aralığındaki faturaları getirir
    public List<Invoice> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findByDateBetween(startDate, endDate);
    }

    // Belirli bir tedarikçiye ait ödenmemiş faturaların toplam borcunu hesaplar
    public double calculateTotalDebtBySupplier(String supplierName) {
        Supplier supplier = supplierRepository.findByName(supplierName);
        if (supplier != null) {
            List<Invoice> unpaidInvoices = getUnpaidInvoicesBySupplier(supplierName);
            return unpaidInvoices.stream().mapToDouble(Invoice::getAmount).sum();
        }
        return 0; // Tedarikçi bulunamazsa 0 döner
    }

    // Ödenmemiş faturaların toplam tutarını hesaplar
    public double calculateTotalDebt() {
        List<Invoice> unpaidInvoices = getUnpaidInvoices();
        return unpaidInvoices.stream().mapToDouble(Invoice::getAmount).sum();
    }

    public List<Invoice> getInvoicesBySupplierId(long supplierId) {
        return invoiceRepository.findBySupplierId(supplierId);
    }



}
