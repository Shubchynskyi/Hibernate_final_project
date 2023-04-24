package util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    //************************** For Config **************************//
    public static final String ENTITY_PACKAGE = "entity";
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;


    //************************** For DB **************************//
    public static final String ID = "ID";
    public static final String CITY = "city";
    public static final String COUNTRY_ID = "country_id";
    public static final String COUNTRY = "country";
    public static final String CONTINENT = "continent";
    public static final String CAPITAL = "capital";
    public static final String COUNTRY_LANGUAGE = "country_language";


    //************************** Logger **************************//
    public static final String HIBERNATE_CONFIG_UPDATED = "Hibernate configuration updated, next classes was adding: ";
    public static final String SESSION_WAS_CLOSED = "session was closed";
    public static final String START_REDIS_CLIENT = "start redis client";
    public static final String ERROR_SENDING_TO_REDIS = "error sending to Redis";
    public static final String SENDING_TO_REDIS_WAS_SUCCESSFUL = "sending to Redis was successful";
    public static final String REDIS_CLIENT_SHUTDOWN_WAS_SUCCESSFUL = "Redis client shutdown was successful";
    public static final String START_REDIS_TEST = "Start redis test";
    public static final String END_REDIS_TEST = "End redis test";
    public static final String START_MYSQL_TEST = "Start mysql test";
    public static final String END_MYSQL_TEST = "End mysql test";
    public static final String REDIS_TEST = "Redis test";
    public static final String MY_SQL_TEST = "MySQL test";


    //************************** General **************************//
    public static final String NEW_LINE = "\n";
    public static final String MESSAGE_PATTERN = "%s:\t%d ms";
    public static final String UNUSED = "unused";
}
