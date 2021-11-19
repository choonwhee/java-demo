package com.demo.test.common.service;

import com.demo.test.common.jpa.CustomRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BaseAutoServiceImpl<T, IDT extends Serializable, R extends CustomRepository<T, IDT>> implements BaseAutoService<T, IDT, R> {
    Logger logger = LoggerFactory.getLogger(BaseAutoServiceImpl.class);

    private R repo;
    private Class entityClass;

    private Method entityGetIdMethod;
    private Map<Class, Method> dtoGetIdMethodCache = new HashMap<>();
    private List<Class> dtosWithoutGetId = new ArrayList<>();

    protected Method getEntityGetIdMethod() {
        return entityGetIdMethod;
    }

    public BaseAutoServiceImpl(Class entityClass, R repo) {
        this.entityClass = entityClass;
        this.repo = repo;
    }

    public R getRepo() {
        return repo;
    }

    public Class getEntityClass() {
        return entityClass;
    }


    public T saveDto(Object dto) {
        cacheDtoGetIdMethodByIntrospection(dto.getClass());
        logger.info("Save Entity with DTO");
        try {
            IDT id;
            if (!dtosWithoutGetId.contains(dto.getClass()))
             id = (IDT) dtoGetIdMethodCache.get(dto.getClass()).invoke(dto);
            else id = null;
            if (id == null) {
                ModelMapper mm = new ModelMapper();
                mm.getConfiguration().addValueReader(new IntrospectionValueReader());
                Object entity = mm.map(dto, getEntityClass());
                return getRepo().save((T)entity);
            } else {
                Optional<T> optionalEntity = getRepo().findById(id);
                if (optionalEntity.isPresent()) {
                    T existingEntity = optionalEntity.get();
                    ModelMapper mm = new ModelMapper();
                    mm.getConfiguration().addValueReader(new IntrospectionValueReader());
                    mm.map(dto, existingEntity);

                    return getRepo().save(existingEntity);
                } else {
                    throw new RuntimeException("Entity (ID: " + id + ") Not Found");
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    public T update(T updateEntity) {
        logger.info("Update Entity");
        cacheGetIdMethodByIntrospection(updateEntity.getClass());
        try {
            T savedEntity;
            IDT id = (IDT) entityGetIdMethod.invoke(updateEntity);
            if (id == null) {
                throw new RuntimeException("ID cannot be null");
            }
            Optional<T> optionalEntity = getRepo().findById(id);
            if (optionalEntity.isPresent()) {
                T existingEntity = optionalEntity.get();
                ModelMapper mm = new ModelMapper();
                mm.map(updateEntity, existingEntity);

                savedEntity = getRepo().save(existingEntity);
            } else {
                throw new RuntimeException("Entity (ID: " + id + ") Not Found");
            }
            return savedEntity;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public T updateDto(Object dto) {
        logger.info("Update DTO");
        cacheDtoGetIdMethodByIntrospection(dto.getClass());
        try {
            T savedEntity;
            IDT id = null;
            if (!dtosWithoutGetId.contains(dto.getClass()))
                id = (IDT) dtoGetIdMethodCache.get(dto.getClass()).invoke(dto);
            if (id == null) {
                throw new RuntimeException("ID cannot be null or DTO must have matching getId Method with entity");
            }
            Optional<T> optionalEntity = getRepo().findById(id);
            if (optionalEntity.isPresent()) {
                T existingEntity = optionalEntity.get();
                ModelMapper mm = new ModelMapper();
                mm.getConfiguration().addValueReader(new IntrospectionValueReader());
                mm.map(dto, existingEntity);

                savedEntity = getRepo().save(existingEntity);
            } else {
                throw new RuntimeException("Entity (ID: " + id + ") Not Found");
            }
            return savedEntity;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void cacheDtoGetIdMethodByIntrospection(Class dtoClass) {
        if (!dtosWithoutGetId.contains(dtoClass) && dtoGetIdMethodCache.get(dtoClass) == null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(dtoClass);
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    if ("id".equalsIgnoreCase(pd.getName())) {
                        dtoGetIdMethodCache.put(dtoClass, pd.getReadMethod());
                        break;
                    }
                }
                if (entityGetIdMethod == null) {
                    dtosWithoutGetId.add(dtoClass);
                }
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void cacheGetIdMethodByIntrospection(Class entityClass) {
        if (entityGetIdMethod == null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(entityClass);
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    if ("id".equalsIgnoreCase(pd.getName())) {
                        entityGetIdMethod = pd.getReadMethod();
                        break;
                    }
                }
                if (entityGetIdMethod == null) {
                    throw new RuntimeException("Could not find getId method in entity.");
                }
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*@Override
    public T findById(IDT id) {
        return repo.findById(id).get();
    }

    @Override
    public List<T> search(Object searchDto) {
        return repo.search(searchDto);
    }

    @Override
    public List<T> simpleSearch(Map<String, Object> searchParamMap) {
        return repo.simpleSearch(searchParamMap);
    }*/


}
