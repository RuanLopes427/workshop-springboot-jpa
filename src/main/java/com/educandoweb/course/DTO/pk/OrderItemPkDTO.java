package com.educandoweb.course.DTO.pk;

import com.educandoweb.course.entities.Order;
import com.educandoweb.course.entities.Product;

import java.io.Serializable;


public class OrderItemPkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Order order;
    private Product product;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
