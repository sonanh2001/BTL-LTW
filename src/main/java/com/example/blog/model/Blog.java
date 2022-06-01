package com.example.blog.model;

import com.example.blog.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name="blogs")
@NoArgsConstructor
@AllArgsConstructor
public class Blog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogId;
    private String title;
    @Column(columnDefinition = "text not null")
    private String content;
    private String imageTitle;
    @CreationTimestamp
    private Date createdAt;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "userId")
    private User userBlog;
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name="blogs_categories",
            joinColumns = {@JoinColumn(name="blog_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")})
    Set<Category> categories=new HashSet<>();
    @ManyToMany(mappedBy = "likedBlog")
    Set<User> likedUser=new HashSet<>();

    @OneToMany(mappedBy = "blogComment",cascade = CascadeType.ALL)
    private Set<Comment> comments=new HashSet<>();
    public String timeConvert(){
        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String[] data= dateFormat.format(createdAt).split("\\s+");
        String date=data[0]+" lúc "+data[1];
        return date;
    }
    public void removeLikedUser(User user){
        this.likedUser.remove(user);
        user.getLikedBlog().remove(this);
    }
    public void addLikedUser(User user){
        this.likedUser.add(user);
        user.getLikedBlog().add(this);
    }
    public Boolean checkLike(String name){
        for (User user:likedUser
             ) {
            if(user.getUsername().equals(name)){
                return true;
            }
        }
        return false;
    }
}
