package dao;

import entity.City;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import util.Constants;

import java.util.List;

public class CityDAO {
    private final SessionFactory sessionFactory;

    public CityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<City> getAll(int offset, int limit) {
        String hql = """
                SELECT c FROM City c
                """;
        Query<City> query = sessionFactory.getCurrentSession().createQuery(hql, City.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public int getTotalCount() {
        String hql = """
                SELECT COUNT(c) FROM City c
                """;
        Query<Long> query = sessionFactory.getCurrentSession().createQuery(hql, Long.class);
        return Math.toIntExact(query.uniqueResult());
    }

    public City getById(Integer id) {
        String hql = """
                SELECT c FROM City c
                JOIN FETCH c.country
                WHERE c.id = :ID
                """;
        Query<City> query = sessionFactory.getCurrentSession().createQuery(hql, City.class);
        query.setParameter(Constants.ID, id);
        return query.getSingleResult();
    }
}
