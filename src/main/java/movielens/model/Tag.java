package movielens.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@ToString
@Entity
@Table(name="tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter Long id;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @Getter @Setter User user;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @Getter @Setter Movie movie;

    @Getter @Setter String tag;
    @Getter @Setter Date date;



}