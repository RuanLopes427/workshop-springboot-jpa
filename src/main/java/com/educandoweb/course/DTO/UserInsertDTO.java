package com.educandoweb.course.DTO;

public class UserInsertDTO extends UserDTO{

    private String password;

    public UserInsertDTO(){
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
