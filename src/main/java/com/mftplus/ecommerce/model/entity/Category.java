package com.mftplus.ecommerce.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "categoryEntity")
@Table(name = "category_tbl")
public class Category extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "c_name", nullable = false, unique = true, length = 20)
    @Pattern(regexp = "^[A-Za-z]{3,20}$",message = "incorrect name!")
    private String name;

    //todo
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", orphanRemoval = true)
    private List<Category> childCategories = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private List<Product> products = new ArrayList<>();

    public List<String> getCategoryPath() {
        List<String> categoryPath = new ArrayList<>();
        categoryPath.add(this.getName());
        Category currentCategory = this;

        while (currentCategory.getParentCategory() != null) {
            categoryPath.add(currentCategory.getParentCategory().getName());
//            categoryPath = currentCategory.getParentCategory().getName() + " > " + categoryPath;
            currentCategory = currentCategory.getParentCategory();
        }

        return categoryPath;
    }

}