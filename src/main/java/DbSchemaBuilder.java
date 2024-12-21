import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DbSchemaBuilder {
    static void connectToDb(){
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session ses = sessionFactory.openSession();
        ses.close();

    }
    public static void main(String[] args) {
        connectToDb();
    }
}