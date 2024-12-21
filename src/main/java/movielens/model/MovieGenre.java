package movielens.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="movie_genres")
public class MovieGenre {
    @Embeddable
    public static class MovieGenreId implements Serializable {
        @ManyToOne
        private Movie movie;
        private String genre;

        public MovieGenreId(Movie movie, String genre){
            this.setMovie(movie);
            this.setGenre(genre);
        }

        public MovieGenreId() {

        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MovieGenreId)) return false;
            MovieGenreId other = (MovieGenreId) o;
            return Objects.equals(other.getGenre(), this.getGenre()) &&
                    Objects.equals(this.getMovie(), other.getMovie());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getGenre(), getMovie().getId());
        }


        public Movie getMovie() {
            return movie;
        }
        public void setMovie(Movie movie) {
            this.movie = movie;
        }
        public String getGenre() {
            return genre;
        }
        public void setGenre(String genre) {
            this.genre = genre;
        }
    }

    @EmbeddedId
    MovieGenre.MovieGenreId movieGenreId = new MovieGenreId();

    public MovieGenre(Movie movie, String genre) {
        this.setMovie(movie);
        this.setGenre(genre);
    }

    public MovieGenre() {

    }

    public Movie getMovie() {
        return movieGenreId.movie;
    }

    public void setMovie(Movie movie) {
        movieGenreId.movie = movie;
    }

    public String getGenre() {
        return movieGenreId.genre;
    }

    public void setGenre(String genre) {
        movieGenreId.genre = genre;
    }

    @Override
    public String toString(){
        return movieGenreId.getGenre();
    }


}
