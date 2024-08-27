package com.berfu.arikanmarket.controller;

import com.berfu.arikanmarket.dto.SupplierDTO;
import com.berfu.arikanmarket.entity.Supplier;
import com.berfu.arikanmarket.mapper.SupplierMapper;
import com.berfu.arikanmarket.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/all")
    public List<SupplierDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers().stream()
                .map(SupplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable long id) {
        Optional<Supplier> supplier = supplierService.getSupplierById(id);
        return supplier.map(value -> ResponseEntity.ok(SupplierMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO) {
        Supplier savedSupplier = supplierService.saveSupplier(SupplierMapper.toEntity(supplierDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(SupplierMapper.toDTO(savedSupplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable long id, @RequestBody SupplierDTO supplierDTO) {
        if (supplierService.getSupplierById(id).isPresent()){
            Supplier updatedSupplier = supplierService.saveSupplier(SupplierMapper.toEntity(supplierDTO));
            return ResponseEntity.ok(SupplierMapper.toDTO(updatedSupplier));
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable long id) {
        if (supplierService.getSupplierById(id).isPresent()){
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
