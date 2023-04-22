import com.fasterxml.jackson.core.JsonProcessingException;
import dao.CityDAO;
import dao.CountryDAO;
import entity.City;
import entity.Country;
import entity.CountryLanguage;
import hibernate.SessionFactoryCreator;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import redis.CityCountry;
import redis.Language;
import redis.RedisService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Main {

    private final SessionFactory sessionFactory;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final RedisService redisService;

    public Main(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.cityDAO = new CityDAO(sessionFactory);
        this.countryDAO = new CountryDAO(sessionFactory);
        this.redisService = new RedisService();
    }

    public static void main(String[] args) {
        Main main = new Main(new SessionFactoryCreator().getSessionFactory());

        List<City> allCities = main.fetchData(main);
        List<CityCountry> preparedData = main.transformData(allCities);
        main.redisService.pushToRedis(preparedData);

        //закроем текущую сессию, чтоб точно делать запрос к БД, а не вытянуть данные из кэша
        main.sessionFactory.getCurrentSession().close();

        //выбираем случайных 10 id городов
        //так как мы не делали обработку невалидных ситуаций, используй существующие в БД id
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        System.out.println("start redis test");
        long startRedis = System.currentTimeMillis();
        main.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();
        System.out.println("end redis test");

        System.out.println("start mysql test");
        long startMysql = System.currentTimeMillis();
        main.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();
        System.out.println("end mysql test");

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        main.shutdown();
    }

    private void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityDAO.getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }

    private void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisService.client.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    redisService.mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisService.client)) {
            redisService.client.shutdown();
        }
    }

    public List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(city -> {
            CityCountry res = new CityCountry();
            res.setId(city.getId());
            res.setName(city.getName());
            res.setPopulation(city.getPopulation());
            res.setDistrict(city.getDistrict());

            Country country = city.getCountry();
            res.setAlternativeCountryCode(country.getCode_2());
            res.setContinent(country.getContinent());
            res.setCountryCode(country.getCode());
            res.setCountryName(country.getName());
            res.setCountryPopulation(country.getPopulation());
            res.setCountryRegion(country.getRegion());
            res.setCountrySurfaceArea(country.getSurfaceArea());
            Set<CountryLanguage> countryLanguages = country.getLanguages();
            Set<Language> languages = countryLanguages.stream().map(o -> {
                Language language = new Language();
                language.setLanguage(o.getLanguage());
                language.setIsOfficial(o.getIsOfficial());
                language.setPercentage(o.getPercentage());
                return language;
            }).collect(Collectors.toSet());
            res.setLanguages(languages);

            return res;
        }).collect(Collectors.toList());
    }

    // TODO move ???
    private List<City> fetchData(Main main) {
        try (Session session = main.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();
            List<Country> countries = main.countryDAO.getAll();

            int totalCount = main.cityDAO.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityDAO.getAll(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

}
