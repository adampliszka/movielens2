import movielens.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.sql.Date;
import java.sql.Timestamp;

public class DBFeeder {
    static void deleteAll() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        Transaction t = ses.beginTransaction();

        Query query = ses.createQuery("delete Rating");
        int result = query.executeUpdate();
        if (result > 0) {
            System.out.println("Ratings were removed");
        }
        query = ses.createQuery("delete Tag");
        result = query.executeUpdate();
        if (result > 0) {
            System.out.println("Tags were removed");
        }
        query = ses.createQuery("delete MovieGenre");
        result = query.executeUpdate();
        if (result > 0) {
            System.out.println("Movie genres were removed");
        }
        query = ses.createQuery("delete Movie");
        result = query.executeUpdate();
        if (result > 0) {
            System.out.println("Movies were removed");
        }
        query = ses.createQuery("delete User");
        result = query.executeUpdate();
        if (result > 0) {
            System.out.println("Users were removed");
        }
        t.commit();
        ses.close();
    }
    static void feedUsers() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        Transaction t = ses.beginTransaction();

        CSVReader reader = null;
        try {
            reader = new CSVReader("users.csv");
            while(reader.next()) {
                User user = new User();
                user.setId(reader.getLong("userId"));
                user.setForename(reader.get("foreName"));
                user.setSurname(reader.get("surName"));
                user.setEmail(reader.get("email"));
                ses.save(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        t.commit();
        ses.close();
    }
    static void feedMovies(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        Transaction t = ses.beginTransaction();

        CSVReader reader = null;
        try {
            reader = new CSVReader("movies.csv");
            while(reader.next()) {
                Movie movie = new Movie();
                movie.setId(reader.getLong("movieId"));
                movie.setTitle(reader.get("title"));
                String[] genres = reader.get("genres").split("\\|");
                Set<MovieGenre> genreList = new HashSet<>();
                for (String s : genres) {
                    MovieGenre genre = new MovieGenre(movie, s);
                    genreList.add(genre);
                }
                movie.setGenreList(genreList);
                ses.save(movie);
                for(MovieGenre g : genreList){
                    ses.save(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        t.commit();
        ses.close();
    }
    static void feedTags(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        Transaction t = ses.beginTransaction();

        CSVReader reader = null;
        try {
            reader = new CSVReader("tags.csv");
            while(reader.next()) {
                //userId,movieId,tag,timestamp
                //2,60756,funny,1445714994
                Tag tag = new Tag();
                Long userId = reader.getLong("userId");
                Long movieId = reader.getLong("movieId");

                Movie movie = ses.get(Movie.class,movieId);
                User user = (User)ses.get(User.class,userId);
                tag.setMovie(movie);
                tag.setUser(user);
                tag.setTag(reader.get("tag"));
                tag.setDate(new Date(reader.getLong("timestamp")*1000));

                user.addTag(tag);
                movie.addTag(tag);

                ses.persist(tag);
                ses.persist(user);
                ses.persist(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        t.commit();
        ses.close();
    }

    static void feedRatings(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        Transaction t = ses.beginTransaction();

        CSVReader reader = null;
        try {
            reader = new CSVReader("ratings.csv");
            while(reader.next()) {
                //userId,movieId,rating,timestamp
                //1,1,4.0,964982703
                Rating rating = new Rating();

                Long userId = reader.getLong("userId");
                Long movieId = reader.getLong("movieId");
                Movie movie = ses.get(Movie.class,movieId);
                User user = (User)ses.get(User.class,userId);

                rating.setMovie(movie);
                rating.setUser(user);
                rating.setRating(reader.getDouble("rating"));
                rating.setDate(new Date(reader.getLong("timestamp")*1000));





                user.addRating(rating);
                movie.addRating(rating);

                ses.persist(rating);
                ses.persist(user);
                ses.persist(movie);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        t.commit();
        ses.close();
    }

    static void check(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        for(var cls: Arrays.asList("User","Movie","MovieGenre","Tag","Rating")){
            Query query = ses.createQuery("select count(*) from "+cls);
            Long count = (Long)query.uniqueResult();
            System.out.println(String.format("%s:%d",cls,count));
        }

        ses.close();
    }

    public static void main(String[] args) {
        deleteAll();
        feedUsers();
        feedMovies();
        feedTags();
        feedRatings();
        check();
    }
}
