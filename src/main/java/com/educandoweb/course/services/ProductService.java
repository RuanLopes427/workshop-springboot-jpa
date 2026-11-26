package com.educandoweb.course.services;

import com.educandoweb.course.DTO.ProductDTO;
import com.educandoweb.course.entities.Product;
import com.educandoweb.course.repositories.ProductRepository;
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
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll(){
        List<Product> list = productRepository.findAll();
        return list.stream().map(ProductDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Product obj = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new ProductDTO(obj);
    }

    public ProductDTO insert(ProductDTO obj){
        Product product = new Product();
        copyDtoToEntity(product, obj);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
   public void delete(Long id){
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
            } else {
                throw new ResourceNotFoundException(id);
            }
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException(e.getMessage());
        }
  }

  public ProductDTO update(Long id, ProductDTO obj){
        Product product = productRepository.getReferenceById(id);
        copyDtoToEntity(product, obj);
        return new ProductDTO(productRepository.save(product));
  }

    private void copyDtoToEntity(Product product, ProductDTO obj) {
        product.setName(obj.getName());
        product.setDescription(obj.getDescription());
        product.setImgUrl(obj.getImgUrl());
        product.setPrice(obj.getPrice());
    }
}
