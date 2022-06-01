package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto implements Serializable {
    private Long categoryId;
    @NotEmpty
    @Length(min=4)
    private String name;
    private Boolean isEdit=false;
}
