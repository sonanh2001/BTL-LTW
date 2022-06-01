package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto implements Serializable {
    @NotEmpty
    @Pattern(regexp = "(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})")
    private String phone;
    @NotEmpty
    private String address;
    @NotEmpty
    private String fullName;
}
