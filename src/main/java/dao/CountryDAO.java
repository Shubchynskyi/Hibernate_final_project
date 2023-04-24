package dao;

import entity.Country;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CountryDAO {
    private final SessionFactory sessionFactory;

    public CountryDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Country> getAll() {
        String hql = """
                SELECT c FROM Country c
                JOIN FETCH c.languages
                """;
        Query<Country> query = sessionFactory.getCurrentSession().createQuery(hql, Country.class);
        return query.list();
    }
}
