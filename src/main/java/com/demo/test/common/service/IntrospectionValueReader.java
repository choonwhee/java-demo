package com.demo.test.common.service;

import com.demo.test.common.util.BeanIntrospectionUtil;
import org.modelmapper.spi.ValueReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class IntrospectionValueReader implements ValueReader<Object> {
    Logger logger = LoggerFactory.getLogger(IntrospectionValueReader.class);

    private Map<Class, Map<String, Method>> readMethodMap = new HashMap<>();
    private Map<Class, List<String>> memberNamesMap = new HashMap<>();

    @Override
    public Object get(Object source, String memberName) {
        try {
            Class sourceClass = source.getClass();
            Map<String, Method> methodMapForClass = readMethodMap.get(sourceClass);
            Method readMethod = null;
            if (methodMapForClass != null)
                 readMethod = methodMapForClass.get(memberName);
            else {
                methodMapForClass = new HashMap<>();
                readMethodMap.put(sourceClass, methodMapForClass);
            }
            if (readMethod == null) {
                BeanInfo bi = Introspector.getBeanInfo(sourceClass);
                for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                    if (memberName.equals(pd.getName())) {
                        readMethod = pd.getReadMethod();
                        methodMapForClass.put(memberName, readMethod);
                        break;
                    }
                }
                if (readMethod == null) throw new RuntimeException("Can't get memberName(" + memberName + ") from source(" + source + ")");
            } else logger.info(sourceClass + "get member("+memberName+") Cache Hit");
            return readMethod.invoke(source);

        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Member getMember(Object source, String memberName) {
        final Object value = get(source, memberName);
        Class<?> memberClass = value != null ? value.getClass() : Object.class;
        if (!BeanIntrospectionUtil.isSimpleType(memberClass))
            return new Member<Object>(memberClass) {

                @Override
                public Object getOrigin() {
                    return value;
                }

                @Override
                public Object get(Object source, String memberName) {
                    return IntrospectionValueReader.this.get(source, memberName);
                }
            };

        return new Member<Object>(memberClass) {
            @Override
            public Object getOrigin() {
                return null;
            }

            @Override
            public Object get(Object source, String memberName) {
                return IntrospectionValueReader.this.get(source, memberName);
            }
        };
    }

    @Override
    public Collection<String> memberNames(Object source) {
        try {
            Class sourceClass = source.getClass();
            List<String> memberNames = memberNamesMap.get(sourceClass);
            if (memberNames == null) {
                memberNames = new ArrayList<>();
                BeanInfo bi = Introspector.getBeanInfo(source.getClass());
                for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                    if (!BeanIntrospectionUtil.isStructuralProperty(pd.getName())) memberNames.add(pd.getName());
                }
                memberNamesMap.put(sourceClass, memberNames);
            } else logger.info("memberNameCacheHit");
            logger.info(source.getClass() + "member names: " + memberNames);
            return memberNames;
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}


