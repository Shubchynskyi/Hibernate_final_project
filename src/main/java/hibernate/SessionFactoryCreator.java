package hibernate;

import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.reflections.Reflections;
import util.Constants;

import java.util.Set;

@Slf4j
public class SessionFactoryCreator {
    private final SessionFactory sessionFactory;

    public SessionFactoryCreator() {
        Configuration configuration = new Configuration();
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

        Reflections reflections = new Reflections(Constants.ENTITY_PACKAGE);
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
        StringBuilder messageForLogger = new StringBuilder(Constants.HIBERNATE_CONFIG_UPDATED)
                .append(Constants.NEW_LINE);
        for (Class<?> entityClass : entityClasses) {
            configuration.addAnnotatedClass(entityClass);
            messageForLogger.append(entityClass.getSimpleName())
                    .append(Constants.NEW_LINE);
        }
        log.info(messageForLogger.toString());
        sessionFactory = configuration.buildSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
