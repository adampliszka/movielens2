package movielens.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name="ratings")

public class Rating  {
    @Data
    @Embeddable
    public static class RatingId implements Serializable {
        @ManyToOne
        private User user;
        @ManyToOne
        private Movie movie;
        public RatingId(Movie movie, User user){
            this.setMovie(movie);
            this.setUser(user);
        }

        public RatingId() {

        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Rating.RatingId)) return false;
            Rating.RatingId other = (Rating.RatingId) o;
            return Objects.equals(other.getUser(), this.getUser()) &&
                    Objects.equals(this.getMovie(), other.getMovie());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getUser().getId(), getMovie().getId());
        }


        public Movie getMovie() {
            return movie;
        }
        public void setMovie(Movie movie) {
            this.movie = movie;
        }
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
    }

    @EmbeddedId
    RatingId ratingId = new RatingId();

    @Getter @Setter double rating;
    @Getter @Setter Date date;

    public void setUser(User user){
        ratingId.setUser(user);
    }
    public User getUser(){
        return ratingId.getUser();
    }
    public void setMovie(Movie movie){
        ratingId.setMovie(movie);
    }
    public Movie getMovie(){
        return ratingId.getMovie();
    }

    @Override
    public String toString(){
        return "rating: %f, date: %s".formatted(rating, date.toString());
    }

}
