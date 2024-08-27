package com.berfu.arikanmarket.mapper;

import com.berfu.arikanmarket.dto.SupplierDTO;
import com.berfu.arikanmarket.entity.Supplier;

import java.util.stream.Collectors;

public class SupplierMapper {

    public static SupplierDTO toDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        if (supplier.getInvoices() != null) {
            dto.setInvoices(supplier.getInvoices().stream().map(InvoiceMapper::toDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    public static Supplier toEntity(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setId(dto.getId());
        supplier.setName(dto.getName());
        // Burada InvoiceMapper'ı kullanarak Invoice'leri ekleyebiliriz
        return supplier;
    }
}
