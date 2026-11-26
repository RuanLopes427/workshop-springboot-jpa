package com.educandoweb.course.DTO;

import com.educandoweb.course.entities.Order;
import com.educandoweb.course.entities.enums.OrderStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private Integer orderStatus;
    private UserDTO client;
    private Set<OrderItemDTO> items = new HashSet<>();
    private PaymentDTO payment;

    public OrderDTO() {
    }

    public OrderDTO(Long id, Instant moment, OrderStatus orderStatus, UserDTO client) {
        this.id = id;
        this.moment = moment;
        setOrderStatus(orderStatus);
        this.client = client;
    }

    public OrderDTO(Order order){
        this.id = order.getId();
        this.moment = order.getMoment();
        this.orderStatus = order.getOrderStatus().getCode();
        this.client = new UserDTO(order.getClient());
        this.items = order.getItems().stream().
                map(OrderItemDTO::new).
                collect(Collectors.toSet());
        if(order.getPayment() != null){
            this.payment = new PaymentDTO(order.getPayment());
        }else{
            this.payment = null;
        }

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

    public UserDTO getClient() {
        return client;
    }

    public void setClient(UserDTO client) {
        this.client = client;
    }

    public OrderStatus getOrderStatus() {
        return OrderStatus.valueOf(orderStatus);
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        if (orderStatus != null) {
            this.orderStatus = orderStatus.getCode();
        }
    }

    public Set<OrderItemDTO> getItems(){
        return items;
    }

    public Double getTotal() {
        Double sum = 0.0;

        for (OrderItemDTO x : items) {
            sum += x.getSubTotal();
        }
        return sum;
    }
}



