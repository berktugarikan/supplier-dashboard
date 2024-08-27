package com.berfu.arikanmarket.repository;

import com.berfu.arikanmarket.entity.Invoice;
import com.berfu.arikanmarket.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    // Tüm faturaları bulur
    List<Invoice> findAll();

    // Ödenmemiş faturaları bulur (paymentStatus = false)
    List<Invoice> findByPaymentStatusFalse();

    // Ödenmiş faturaları bulur (paymentStatus = true)
    List<Invoice> findByPaymentStatusTrue();

    // Belirli bir tedarikçiye göre ödenmemiş faturaları bulur
    List<Invoice> findBySupplierAndPaymentStatusFalse(Supplier supplier);

    // Belirli bir tarih aralığındaki tüm faturaları bulur
    List<Invoice> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Tedarikçiye ait ödenmemiş faturaları bulur
    List<Invoice> findBySupplier(Supplier supplier);

    List<Invoice> findBySupplierId(long supplierId);

    @Query("SELECT i FROM Invoice i WHERE i.date BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}