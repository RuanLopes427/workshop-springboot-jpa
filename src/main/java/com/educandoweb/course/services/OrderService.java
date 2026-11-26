package com.educandoweb.course.services;

import com.educandoweb.course.DTO.OrderDTO;
import com.educandoweb.course.entities.Order;
import com.educandoweb.course.repositories.OrderRepository;
import com.educandoweb.course.services.exceptions.DatabaseException;
import com.educandoweb.course.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        List<Order> list = orderRepository.findAll();
        return list.stream().map(OrderDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
       Order obj = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new OrderDTO(obj);
    }

    public OrderDTO insert(OrderDTO obj) {
        Order order = new Order();
        copyDtoToEntity(order, obj);
        order = orderRepository.save(order);
       return new OrderDTO(order);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        try {
            if (orderRepository.existsById(id)) {
                orderRepository.deleteById(id);
            } else {
                throw new ResourceNotFoundException(id);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public OrderDTO update(Long id, OrderDTO obj){
        Order order = orderRepository.getReferenceById(id);
        copyDtoToEntity(order, obj);
        return new OrderDTO(orderRepository.save(order));
    }

    private void copyDtoToEntity(Order order, OrderDTO obj) {
        order.setOrderStatus(obj.getOrderStatus());

    }

}
