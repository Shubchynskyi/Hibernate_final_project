package dao;

import entity.City;
import entity.Country;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import util.Constants;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class DaoService {
    private final SessionFactory sessionFactory;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public DaoService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.cityDAO = new CityDAO(sessionFactory);
        this.countryDAO = new CountryDAO(sessionFactory);
    }

    public void closeSession() {
        sessionFactory.getCurrentSession().close();
        log.info(Constants.SESSION_WAS_CLOSED);
    }

    public List<City> getData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();
            @SuppressWarnings("unused") // needed to reduce the number of queries
            List<Country> countries = countryDAO.getAll();

            int totalCount = cityDAO.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityDAO.getAll(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }
}
