package com.example.demo.Model;

import com.example.demo.Controller.UserController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String variantId;


    @NotEmpty
    private String name;

    @NotNull
    private Double price;

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    private Set<User> users = new HashSet<>();



    public UserController getUsers() {
        return null;
    }
}
