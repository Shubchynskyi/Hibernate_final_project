import com.fasterxml.jackson.core.JsonProcessingException;
import dao.DaoService;
import entity.City;
import entity.Country;
import entity.CountryLanguage;
import hibernate.SessionFactoryCreator;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.hibernate.Session;
import redis.CityCountry;
import redis.Language;
import redis.RedisService;
import util.Constants;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
public class Main {

    private final DaoService daoService;
    private final RedisService redisService;

    public Main(RedisService redisService, DaoService daoService) {
        this.daoService = daoService;
        this.redisService = redisService;
    }

    public static void main(String[] args) {
        Main main = new Main(new RedisService(), new DaoService(new SessionFactoryCreator().getSessionFactory()));

        List<City> allCities = main.daoService.getData();
        List<CityCountry> preparedData = main.transformData(allCities);
        main.redisService.pushToRedis(preparedData);

        // make the cache unavailable for correct testing
        main.daoService.closeSession();

        // take random real id for tests
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        log.info(Constants.START_REDIS_TEST);
        long startRedis = System.currentTimeMillis();
        main.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();
        log.info(Constants.END_REDIS_TEST);


        log.info(Constants.START_MYSQL_TEST);
        long startMysql = System.currentTimeMillis();
        main.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();
        log.info(Constants.END_MYSQL_TEST);

        log.info(new StringFormattedMessage(Constants.MESSAGE_PATTERN, Constants.REDIS_TEST, (stopRedis - startRedis)).toString());
        log.info(new StringFormattedMessage(Constants.MESSAGE_PATTERN, Constants.MY_SQL_TEST, (stopMysql - startMysql)).toString());

        main.shutdown();
    }


    private void testMysqlData(List<Integer> ids) {
        try (Session session = daoService.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = daoService.getCityDAO().getById(id);
                @SuppressWarnings(Constants.UNUSED) // needed for test
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
        if (nonNull(daoService.getSessionFactory().getCurrentSession())) {
            daoService.closeSession();
        }
        if (nonNull(redisService.client)) {
            redisService.shutdown();
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
}
