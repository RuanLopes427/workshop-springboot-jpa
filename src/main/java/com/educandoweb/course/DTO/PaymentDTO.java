package com.educandoweb.course.DTO;

import com.educandoweb.course.entities.Payment;

import java.io.Serializable;
import java.time.Instant;


public class PaymentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;

    public PaymentDTO(){
    }

    public PaymentDTO(Long id, Instant moment, OrderDTO order) {
        this.id = id;
        this.moment = moment;

    }

    public PaymentDTO(Payment payment){
        this.id = payment.getId();
        this.moment = payment.getMoment();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

}
