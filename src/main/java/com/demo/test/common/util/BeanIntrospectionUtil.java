package com.demo.test.common.util;

import java.sql.Timestamp;
import java.time.*;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class BeanIntrospectionUtil {

    public static boolean isSimpleType(Class propClass) {
        return (propClass == String.class || propClass == Character.class || propClass.isPrimitive()
                || propClass == Boolean.class || propClass.isArray() || propClass == Date.class
                || propClass == LocalDate.class || propClass == LocalTime.class || propClass == LocalDateTime.class
                || propClass == OffsetTime.class || propClass == OffsetDateTime.class || propClass == ZonedDateTime.class
                || propClass == Timestamp.class
                || Number.class.isAssignableFrom(propClass)
                || Collection.class.isAssignableFrom(propClass) || Map.class.isAssignableFrom(propClass));
    }

    public static boolean isStructuralProperty(String propertyName) {
        return ("class".equals(propertyName) || "declaringClass".equals(propertyName));
    }
}
