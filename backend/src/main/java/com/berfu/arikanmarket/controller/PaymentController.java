package com.berfu.arikanmarket.controller;

import com.berfu.arikanmarket.dto.PaymentDTO;
import com.berfu.arikanmarket.entity.Invoice;
import com.berfu.arikanmarket.entity.Payment;
import com.berfu.arikanmarket.mapper.PaymentMapper;
import com.berfu.arikanmarket.service.InvoiceService;
import com.berfu.arikanmarket.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    public PaymentController(PaymentService paymentService, InvoiceService invoiceService) {
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDTO) {
        Optional<Invoice> invoiceOpt = invoiceService.getInvoiceById(paymentDTO.getInvoiceId());
        if (invoiceOpt.isPresent()) {
            Payment payment = PaymentMapper.toEntity(paymentDTO, invoiceOpt.get());
            Payment savedPayment = paymentService.savePayment(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(PaymentMapper.toDTO(savedPayment));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/all")
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments().stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(value -> ResponseEntity.ok(PaymentMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
