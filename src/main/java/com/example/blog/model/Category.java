package com.example.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @Column(columnDefinition = "varchar(50) not null")
    private String name;
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "categories")
    private Set<Blog> blogs=new HashSet<>();
}
