package com.example.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(columnDefinition = "varchar(50) not null")
    private String username;
    @Column(columnDefinition = "varchar(255) not null")
    private String password;
    @Column(columnDefinition = "varchar(100) not null")
    private String email;
    @Column(columnDefinition = "varchar(10) not null")
    private String role;
    @Column(columnDefinition = "varchar(255)")
    private String address;
    @Column(columnDefinition = "varchar(15)")
    private String phone;
    @Column(columnDefinition = "varchar(100)")
    private String fullName;
    @CreationTimestamp
    private Date registeredAt;
    @OneToMany(mappedBy = "userBlog",cascade = {CascadeType.ALL})
    private Set<Blog> blogs=new HashSet<>();
    @OneToMany(mappedBy = "userComment",cascade = {CascadeType.ALL})
    private Set<Comment> comments=new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST})
    @JoinTable(name="liked",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="blog_id")})
    private Set<Blog> likedBlog=new HashSet<>();
    public void removeLikedBlog(Blog blog){
        this.likedBlog.remove(blog);
        blog.getLikedUser().remove(this);
    }
}
