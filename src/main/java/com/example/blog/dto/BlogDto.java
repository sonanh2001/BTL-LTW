package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto {
    private Long blogId;
    @NotEmpty
    @Length(min=5)
    private String title;
    @NotEmpty
    private String content;
    private String imageTitle;
    private Boolean isEdit=false;
    private Set<Long> categoriesBlog=new HashSet<>();
    private String createdAt;
}
