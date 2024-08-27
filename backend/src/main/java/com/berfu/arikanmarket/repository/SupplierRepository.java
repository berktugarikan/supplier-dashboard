package com.berfu.arikanmarket.repository;

import com.berfu.arikanmarket.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Supplier findByName(String name);
}
