package com.educandoweb.course.services;

import com.educandoweb.course.entities.Category;
import com.educandoweb.course.repositories.CategoryRepository;
import com.educandoweb.course.services.exceptions.DatabaseException;
import com.educandoweb.course.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll(){ return categoryRepository.findAll(); }

    public Category findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Category insert(Category obj){
        return categoryRepository.save(obj);
    }

    public void delete(Long id){
        try{
            if(categoryRepository.existsById(id)){
                categoryRepository.deleteById(id);
            }
            else{
                throw new ResourceNotFoundException(id);
            }
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException(e.getMessage());
        }
    }

    public Category update(Long id, Category obj){
        Category cat = categoryRepository.getReferenceById(id);
        updateData(cat, obj);
        return categoryRepository.save(cat);
    }

    private void updateData(Category cat, Category obj) {
        cat.setName(obj.getName());
    }
}
