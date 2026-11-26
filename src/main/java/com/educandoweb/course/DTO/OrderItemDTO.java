package com.educandoweb.course.DTO;

import com.educandoweb.course.entities.Order;
import com.educandoweb.course.entities.OrderItem;
import com.educandoweb.course.entities.Product;

import java.io.Serializable;


public class OrderItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String name;
    private Integer quantity;
    private Double price;

    public OrderItemDTO(){
    }

    public OrderItemDTO(Order order, Product product, Integer quantity, Double price) {
        this.productId = product.getId();
        this.name = product.getName();;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItemDTO(OrderItem orderItem){
        productId = orderItem.getProduct().getId();
        name = orderItem.getProduct().getName();
        price = orderItem.getPrice();
        quantity = orderItem.getQuantity();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSubTotal(){
        return quantity * price;
    }

}
