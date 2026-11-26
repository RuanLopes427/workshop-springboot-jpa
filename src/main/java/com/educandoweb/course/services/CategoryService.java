package com.educandoweb.course.services;

import com.educandoweb.course.DTO.CategoryDTO;
import com.educandoweb.course.entities.Category;
import com.educandoweb.course.repositories.CategoryRepository;
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
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> list = categoryRepository.findAll();
        return list.stream().map(CategoryDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Category obj = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new CategoryDTO(obj);
    }

    public CategoryDTO insert(CategoryDTO obj){
        Category entity = new Category();
        copyDtoToEntity(entity, obj);
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
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

    public CategoryDTO update(Long id, CategoryDTO obj){
        Category cat = categoryRepository.getReferenceById(id);
        copyDtoToEntity(cat, obj);
        return new CategoryDTO(categoryRepository.save(cat));
    }

    private void copyDtoToEntity(Category cat, CategoryDTO obj) {
        cat.setName(obj.getName());
    }

}
