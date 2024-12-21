package movielens.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
public class User {
    @Id
    @Getter @Setter Long id;
    @Getter @Setter String forename;
    @Getter @Setter String surname;
    @Getter @Setter String email;

    @OneToMany(mappedBy = "user")
    @Getter private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "ratingId.user")
    @Getter private Set<Rating> ratings = new HashSet<>();

    public void addTag(Tag tag) {
        tags.add(tag);
    }
    public void addRating(Rating rating) {
        ratings.add(rating);
    }

    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append(forename);
        b.append(" ");
        b.append(surname);
        b.append(String.format("(%s,id=%d,ratings=%d, tags=%d)",email,id,ratings.size(),tags.size()));
        return b.toString();
    }

}