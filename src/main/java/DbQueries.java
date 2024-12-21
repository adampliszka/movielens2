import movielens.model.Movie;
import movielens.model.Rating;
import movielens.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DbQueries {

    static void sampleQuery() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("from Movie m where (select avg(r.rating) from Rating as r where r.ratingId.movie = m group by r.ratingId.movie) >= 4.6 and size(m.ratings) >= 5",Movie.class);
        List<Movie> movies = q.getResultList();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(movies.size());
        for (var m : movies) {
            System.out.println(m);
        }
        ses.close();
    }

    static void sampleQueryAsStream(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
//        Transaction t = ses.beginTransaction();
        long start = System.nanoTime();
        Query q=ses.createQuery("from Movie");
        List<Movie> movies=q.list();
        movies = movies.stream().filter(m->{
            return
                    m.getRatings().stream().mapToDouble(r-> r.getRating()).average().orElse(0.0)>=4.6
                            && m.getRatings().size()>=5;}).collect(Collectors.toList());
        long end = System.nanoTime();
        System.out.println("Czas stream:" + (end-start)/1e6);

        System.out.println(movies.size());
        for(var m:movies) {
            System.out.println(m);
        }
//        t.commit();
        ses.close();
    }

    static void sampleQueryWithJoin() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("from Movie m inner join Rating r on r.ratingId.movie=m where r.rating = 5",Movie.class);
        q.setMaxResults(300);
        List<Movie> movies = q.getResultList();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(movies.size());
        for (var m : movies) {
            System.out.println(m);
        }
        ses.close();
    }

    static void sampleQueryWithJoin2() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("from Movie m inner join Rating r on r.ratingId.movie=m where r.rating = 5",Object[].class);
        q.setMaxResults(300);
        List<Object[]> moviesRatings = q.getResultList();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(moviesRatings.size());
        for (var m : moviesRatings) {
            System.out.println(m[0]);
            System.out.println(((Rating)m[1]).getRating());
        }
        ses.close();
    }

    static void Q1() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("from User u order by size(u.ratings) desc",User.class);
        q.setMaxResults(10);
        List<User> users = q.getResultList();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(users.size());
        for (var m : users) {
            System.out.println(m);
        }
        ses.close();
    }

    static void Q1AsStream() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q=ses.createQuery("from User");
        List<User> users=q.list();
        users.sort((a,b) -> Integer.compare(b.getRatings().size(), a.getRatings().size()));
        users = users.subList(0,10);

        long end = System.nanoTime();
        System.out.println("Czas stream:" + (end-start)/1e6);

        System.out.println(users.size());
        for(var m:users) {
            System.out.println(m);
        }
        ses.close();
    }

    static void Q2() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("from Movie m inner join MovieGenre mg where mg.movieGenreId.genre='Drama' and mg.movieGenreId.movie=m group by m.id order by m.id ",Movie.class);
        q.setMaxResults(100);
        List<Movie> movies = q.getResultList();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(movies.size());
        for (var m : movies) {
            System.out.println(m);
        }
        ses.close();
    }
    static void Q2AsStream() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q=ses.createQuery("from Movie");
        List<Movie> movies=q.list();

        movies = movies.stream().filter(m->m.getGenreList().stream().anyMatch(g->g.getGenre().equals("Drama"))).collect(Collectors.toList());

        movies = movies.subList(0,100);
        long end = System.nanoTime();
        System.out.println("Czas stream:" + (end-start)/1e6);

        System.out.println(movies.size());
        for(var m:movies) {
            System.out.println(m);
        }
        ses.close();
    }

    static void Q3(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("from Movie as m join m.tags as tags join tags.user as u where u.forename='Richard' and u.surname='Oliver' group by m.id",Object[].class);
        List<Object[]> movies=q.list();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(movies.size());
        for (var m : movies) {
            System.out.println(m[0]);
        }
        ses.close();
    }

    static void Q3AsStream() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q=ses.createQuery("from Movie");
        List<Movie> movies=q.list();

        movies = movies.stream().filter(m->m.getTags().stream().anyMatch(t->(t.getUser().getForename().equals("Richard")&&t.getUser().getSurname().equals("Oliver")))).collect(Collectors.toList());

        long end = System.nanoTime();
        System.out.println("Czas stream:" + (end-start)/1e6);

        System.out.println(movies.size());
        for(var m:movies) {
            System.out.println(m);
        }
        ses.close();
    }

    static void Q4() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("from Movie as m join m.tags as tags where tags.tag='Leonardo DiCaprio' group by m.id",Movie.class);
        List<Movie> movies = q.getResultList();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(movies.size());
        for (var m : movies) {
            System.out.println(m);
        }
        ses.close();
    }

    static void Q4AsStream() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q=ses.createQuery("from Movie");
        List<Movie> movies=q.list();

        movies = movies.stream().filter(m->m.getTags().stream().anyMatch(t->(t.getTag().equals("Leonardo DiCaprio")))).collect(Collectors.toList());

        long end = System.nanoTime();
        System.out.println("Czas stream:" + (end-start)/1e6);

        System.out.println(movies.size());
        for(var m:movies) {
            System.out.println(m);
        }
        ses.close();
    }

    static void Q5() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q = ses.createQuery("SELECT DISTINCT year(r.date), count(rating) as Year FROM Rating r group by year(r.date)",Object[].class);
        List<Object[]> movies = q.getResultList();
        long end = System.nanoTime();
        System.out.println("Czas DB:" + (end - start) / 1e6);
        System.out.println(movies.size());
        for (var m : movies) {
            System.out.printf("%d %d%n",m[0],m[1]);
        }
        ses.close();
    }

    static void Q5AsStream(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        long start = System.nanoTime();
        Query q=ses.createQuery("from Rating");
        List<Rating> ratings=q.list();
        List<Integer> years = ratings.stream().map(Rating::getDate).map(Date::getYear).distinct().collect(Collectors.toList());
        years.sort(Integer::compare);
        List<Integer> counts = new ArrayList<>();

        for(int i = 0; i<years.size(); i++){
            int finalI = i;
            counts.add((int) ratings.stream().filter(r->(r.getDate().getYear()==years.get(finalI))).count() );
        }


        long end = System.nanoTime();
        System.out.println("Czas stream:" + (end-start)/1e6);

        System.out.println(years.size());
        for(int i = 0; i<years.size(); i++) {
            System.out.printf("%d %d%n",years.get(i)+1900,counts.get(i));
        }
        ses.close();
    }

    public static void main(String[] args) {
        //sampleQuery();
        //sampleQueryAsStream();
        //sampleQueryWithJoin();
        //sampleQueryWithJoin2();
        Q1();
        Q1AsStream();
        Q2();
        Q2AsStream();
        Q3();
        Q3AsStream();
        Q4();
        Q4AsStream();
        Q5();
        Q5AsStream();
    }
}