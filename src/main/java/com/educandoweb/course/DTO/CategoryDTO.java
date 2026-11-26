package com.educandoweb.course.DTO;


import com.educandoweb.course.entities.Category;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class CategoryDTO implements Serializable {

    private static final long  serialVersionUID = 1L;

    private Long id;
    private String name;
    private Set<ProductDTO> products = new HashSet<>();

    public CategoryDTO(){
    }

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public CategoryDTO(Category category){
        this.setId(category.getId());
        this.setName(category.getName());
        this.products = category.getProducts().
                stream().
                map(product -> new ProductDTO(product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getImgUrl())).
                collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Set<ProductDTO> getProducts(){ return this.products; }

}
