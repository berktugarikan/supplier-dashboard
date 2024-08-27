package com.berfu.arikanmarket.mapper;

import com.berfu.arikanmarket.dto.PaymentDTO;
import com.berfu.arikanmarket.entity.Invoice;
import com.berfu.arikanmarket.entity.Payment;

// PaymentMapper.java
public class PaymentMapper {

    public static Payment toEntity(PaymentDTO paymentDTO, Invoice invoice) {
        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setInvoice(invoice);
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentDTO.getPaymentMethod()));
        return payment;
    }

    public static PaymentDTO toDTO(Payment payment) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(payment.getId());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setPaymentDate(payment.getPaymentDate());
        paymentDTO.setInvoiceId(payment.getInvoice().getId());
        paymentDTO.setPaymentMethod(payment.getPaymentMethod().name());
        return paymentDTO;
    }
}


