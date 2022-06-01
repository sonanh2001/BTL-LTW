package com.example.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
@Getter
@Setter
@Entity
@Table(name="comments")
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(columnDefinition = "text not null")
    private String content;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name="userId")
    private User userComment;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name="blogId")
    private Blog blogComment;
    @CreationTimestamp
    private Date createdAt;
    public String timeConvert(){
        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String[] data= dateFormat.format(createdAt).split("\\s+");
        String date=data[0]+" lúc "+data[1];
        return date;
    }

}
