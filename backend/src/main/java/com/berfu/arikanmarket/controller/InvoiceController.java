package com.berfu.arikanmarket.controller;

import com.berfu.arikanmarket.dto.InvoiceDTO;
import com.berfu.arikanmarket.entity.Invoice;
import com.berfu.arikanmarket.entity.Supplier;
import com.berfu.arikanmarket.mapper.InvoiceMapper;
import com.berfu.arikanmarket.service.InvoiceService;
import com.berfu.arikanmarket.service.SupplierService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final SupplierService supplierService;

    public InvoiceController(InvoiceService invoiceService, SupplierService supplierService) {
        this.invoiceService = invoiceService;
        this.supplierService = supplierService;
    }

    @GetMapping("/all")
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getAllInvoices().stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/bySupplierId/{supplierId}")
    public List<InvoiceDTO> getInvoicesBySupplierId(@PathVariable long supplierId) {
        return invoiceService.getInvoicesBySupplierId(supplierId).stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());
    }


    @GetMapping("/unpaid")
    public List<InvoiceDTO> getUnpaidInvoices() {
        return invoiceService.getUnpaidInvoices().stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/paid")
    public List<InvoiceDTO> getPaidInvoices() {
        return invoiceService.getPaidInvoices().stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/totalDebtBySupplier")
    public double getTotalDebtBySupplier(@RequestParam String supplier) {
        return invoiceService.calculateTotalDebtBySupplier(supplier);
    }

    @GetMapping("/dateRange")
    public List<InvoiceDTO> getInvoicesByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return invoiceService.getInvoicesByDateRange(startDate, endDate).stream().map(InvoiceMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/totalDebt")
    public double getTotalDebt() {
        return invoiceService.calculateTotalDebt();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable long id) {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(value -> ResponseEntity.ok(InvoiceMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        Supplier supplier = supplierService.getSupplierById(invoiceDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        Invoice savedInvoice = invoiceService.saveInvoice(InvoiceMapper.toEntity(invoiceDTO, supplier));
        return ResponseEntity.status(HttpStatus.CREATED).body(InvoiceMapper.toDTO(savedInvoice));
    }

    @PutMapping("{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable long id, @RequestBody InvoiceDTO invoiceDTO) {
        if (invoiceService.getInvoiceById(id).isPresent()) {
            Supplier supplier = supplierService.getSupplierById(invoiceDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            Invoice updatedInvoice = invoiceService.saveInvoice(InvoiceMapper.toEntity(invoiceDTO, supplier));
            updatedInvoice.setId(id);
            return ResponseEntity.ok(InvoiceMapper.toDTO(updatedInvoice));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable long id) {
        if (invoiceService.getInvoiceById(id).isPresent()) {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}