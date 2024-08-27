package com.berfu.arikanmarket.mapper;

import com.berfu.arikanmarket.dto.InvoiceDTO;
import com.berfu.arikanmarket.entity.Invoice;
import com.berfu.arikanmarket.entity.Supplier;
import com.berfu.arikanmarket.service.SupplierService;
import java.util.stream.Collectors;

public class InvoiceMapper {
    public static InvoiceDTO toDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setSupplierId(invoice.getSupplier().getId());
        dto.setDate(invoice.getDate());
        dto.setAmount(invoice.getAmount());
        dto.setPaymentStatus(invoice.isPaymentStatus());
        dto.setDescription(invoice.getDescription());
        if (invoice.getPayments() != null) {
            dto.setPayments(invoice.getPayments().stream().map(PaymentMapper::toDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    public static Invoice toEntity(InvoiceDTO dto, Supplier supplier) {
        Invoice invoice = new Invoice();
        invoice.setId(dto.getId());
        invoice.setSupplier(supplier);
        invoice.setDate(dto.getDate());
        invoice.setAmount(dto.getAmount());
        invoice.setPaymentStatus(dto.isPaymentStatus());
        invoice.setDescription(dto.getDescription());
        return invoice;
    }
}
