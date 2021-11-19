package com.demo.test.common.jpa;

import com.demo.test.common.util.BeanIntrospectionUtil;
import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.common.workflow.dto.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CustomRepository<T, ID> {

    Logger logger = LoggerFactory.getLogger(CustomRepositoryImpl.class);

    private final EntityManager entityManager;
    private Map<String, Class> entityPropsCache = null;
    private Map<Prefix,Map<String, Class>> prefixEntityPropsCacheMap = new HashMap<>();

    public CustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public CustomRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    private void populateEntityPropertiesMapIfNull(Class c) {
        if (this.entityPropsCache == null) {
            this.entityPropsCache = new HashMap<>();
            populateEntityPropsMap(c, this.entityPropsCache, "");
        }
    }

    private void populatePrefixEntityPropertiesMapIfNull(Prefix prefix) {
        if (prefix != null && this.prefixEntityPropsCacheMap.get(prefix) == null) {
            Map<String, Class> propsMap = new HashMap<>();
            this.prefixEntityPropsCacheMap.put(prefix, propsMap);
            populateEntityPropsMap(prefix.getPrefixEntityClass(), propsMap, "");
        }
    }

    private void populateEntityPropsMap(Class c, Map<String, Class> propsMap, String parentPropertyName) {
            try {
                BeanInfo info = Introspector.getBeanInfo(c, Object.class);
                for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                    String propName;
                    if (parentPropertyName == null || parentPropertyName.length() == 0) propName = pd.getName();
                    else propName = parentPropertyName + "." + pd.getName();
                    if (pd.getPropertyType().isAnnotationPresent(Entity.class)) {
                        populateEntityPropsMap(pd.getPropertyType(), propsMap, propName);
                    } else {
                        propsMap.put(propName, pd.getPropertyType());
                    }
                }
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
    }

    private void buildPropertyExpression(StringBuilder queryStr, Map<String, Object> params, String propName, String paramName, Object propValue, Operator operator, Prefix prefix) {
        logger.info("Build Property Expression for " + propName);
        String prefixStr;
        if (prefix == null) prefixStr = "e";
        else prefixStr = prefix.getAs();
        if (operator == Operator.BETWEEN || operator == Operator.NOT_BETWEEN) {
            if (propValue.getClass().isArray()) {
                propValue = Arrays.asList((Object[]) propValue);
            }
            if (propValue instanceof Collection) {
                Collection<Object> propVals = (Collection<Object>) propValue;
                if (propVals.size() == 2) {
                    Iterator<Object> i = propVals.iterator();
                    Object valueFrom = i.next();
                    Object valueTo = i.next();

                    // Not exactly consistent implementation to have this logic to pass 2 search values here,
                    // maybe can think of other ways to make code design more consistent
                    queryStr.append(" and ").append(operator.getExpression(prefixStr + "." + propName, ":" + paramName + "From AND :" + paramName + "To "));
                    params.put(paramName + "From", valueFrom);
                    params.put(paramName + "To", valueTo);
                } else {
                    throw new RuntimeException("Between property array or collection must have exact 2 items [fromValue, toValue]");
                }
            } else {
                throw new RuntimeException("Between Operator must be an array or collection");
            }
        } else if ((operator == Operator.MEMBER_OF || operator == Operator.NOT_MEMBER_OF) && (propValue.getClass().isArray() || propValue instanceof Collection)) {
            if (propValue.getClass().isArray()) {
                propValue = Arrays.asList((Object[]) propValue);
            }

            Collection<Object> propVals = (Collection<Object>) propValue;

            // Not exactly consistent implementation to have this logic to loop here,
            // maybe can think of other ways to make code design more consistent
            if (propVals.size() > 0) {
                final Integer[] i = {0};
                queryStr.append(" and (");
                propVals.forEach(propVal -> {
                    if (i[0] > 0) queryStr.append(" or ");
                    queryStr.append(operator.getExpression(prefixStr + "." + propName, ":" + paramName + i[0]));
                    params.put(paramName + i[0], propVal);
                    i[0]++;
                });
                queryStr.append(")");
            }
        } else {
            queryStr.append(" and ").append(operator.getExpression(prefixStr + "." + propName, ":" + paramName));
            params.put(paramName, propValue);
        }
    }

    private void buildQueryFilterStr(StringBuilder queryStr, Map<String, Object> params, Object obj, String parentPropertyName, Boolean allowPrefix) {
        if (obj != null) {
            BeanInfo info;
            try {
                info = Introspector.getBeanInfo(obj.getClass(), Object.class);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Object propValue;
                logger.info("property: " + pd.getName());
                try {
                    propValue = pd.getReadMethod().invoke(obj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                if (propValue != null) {
                    String propName = pd.getName();
                    Class propClass = pd.getPropertyType();
                    if (BeanIntrospectionUtil.isSimpleType(propClass)) {
                        buildPropertyExpressionWithPrePostfix(queryStr, params, propValue, parentPropertyName, allowPrefix, propName, propClass);
                    } else {
                        if (propClass != Class.class) {
                            buildQueryFilterStr(queryStr, params, propValue, propName, allowPrefix);
                        }
                    }
                }
            }
        }
    }

    private void buildSimpleQueryFilterStr(StringBuilder queryStr, Map<String, Object> params, Map<String, Object> searchBy, Boolean allowPrefix) {
logger.info("allowPrefix: " + allowPrefix);
        if (searchBy != null) {
            for (Map.Entry<String, Object> entry : searchBy.entrySet()) {
                Object propValue;
                propValue = entry.getValue();

                if (propValue != null) {
                    String propName = entry.getKey();
                    Class propClass = propValue.getClass();

                    String parentPropertyName = "";
                    int parentChildNameSeparatorPosition = propName.lastIndexOf(".");
                    if (parentChildNameSeparatorPosition != -1) {
                        parentPropertyName = propName.substring(0, parentChildNameSeparatorPosition - 1);
                        propName = propName.substring(parentChildNameSeparatorPosition);
                    }
                    buildPropertyExpressionWithPrePostfix(queryStr, params, propValue, parentPropertyName, allowPrefix, propName, propClass);
                }
            }
        }
    }

    private String removePrefixFromPropName(String propName, Prefix prefix) {
        if (prefix != null) {
            propName = propName.substring(prefix.getPrefix().length());
            propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
            logger.debug("propName without Prefix: " + propName);
        }
        return propName;
    }

    private Prefix extractPrefix(String propName) {
        Prefix prefix = null;
        for (Prefix pf : Prefix.values()) {
            if (pf.getRegexPattern().matcher(propName).find()) {
                logger.debug("prefix matched: " + pf);
                prefix = pf;
                break;
            }
        }
        return prefix;
    }

    private void buildPropertyExpressionWithPrePostfix(StringBuilder queryStr, Map<String, Object> params, Object propValue, String parentPropertyName, boolean allowPrefix, String propName, Class propClass) {
        logger.info("searchPropName: "+propName);
        if (parentPropertyName != null && parentPropertyName.length() > 0) propName = parentPropertyName + "." + propName;

        Prefix prefix = null;
        logger.info("allowPrefix: " + allowPrefix);
        if (allowPrefix) {
            prefix = extractPrefix(propName);
            populatePrefixEntityPropertiesMapIfNull(prefix);
            propName = removePrefixFromPropName(propName, prefix);
        }
        String paramName = propName.replace(".", "_");

        logger.info("parentPropName: " + parentPropertyName);
        logger.info("prefix: " + (prefix == null ? null : prefix.getPrefix()));
        logger.info("propName: " + propName);


        if ((prefix == null && this.entityPropsCache.containsKey(propName)) || (prefix != null && this.prefixEntityPropsCacheMap.get(prefix).containsKey(propName))) {
            logger.info("matched");
            if (propClass == Collection.class) {
                logger.info("matched collection");
                buildPropertyExpression(queryStr, params, propName, paramName, propValue, Operator.IN, prefix);
            } else {
                logger.info("matched non collection");
                buildPropertyExpression(queryStr, params, propName, paramName, propValue, Operator.EQUALS, prefix);
            }
        } else {
            Operator operator = null;
            for (Operator op : Operator.values()) {
                logger.info(op.getValue() + "|" + propName);
                if (op.getRegexPattern().matcher(propName).find()) {
                    logger.info("postfix matched: " + op);
                    operator = op;
                    break;
                }
            }
            if (operator == null) throw new RuntimeException("No matching search property or Invalid Operator (" + propName + ")");

            int propNameEndIndex = operator.getValue().length();
            String propNameWithoutSuffix = propName.substring(0, propName.length() - propNameEndIndex);
            if ((prefix == null && this.entityPropsCache.containsKey(propNameWithoutSuffix))
            || (prefix != null && this.prefixEntityPropsCacheMap.get(prefix).containsKey(propNameWithoutSuffix))) {
                String postFix = propName.substring(propName.length() - propNameEndIndex);
                propName = propNameWithoutSuffix;
                logger.info("propName: " + propName + " | postFix: " + postFix);
                buildPropertyExpression(queryStr, params, propName, paramName, propValue, operator, prefix);
            } else {
                logger.info("Property cannot be matched to any in search target Entity object, Ignoring Property: " + propName);
            }
        }
    }

    @Override
    public List<T> search(Object searchDto) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityQueryFromStr(queryStr);
        Map<String, Object> params = new HashMap<>();
        buildQueryFilterStr(queryStr, params, searchDto, "", false);

        TypedQuery<T> query = createTypedQuery(getEntityQueryStr(queryStr), params);

        return query.getResultList();
    }

    @Override
    public Page<T> search(Pageable pageable, Object searchDto) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityQueryFromStr(queryStr);
        Map<String, Object> params = new HashMap<>();
        buildQueryFilterStr(queryStr, params, searchDto, "", false);

        return queryEntityPage(pageable, queryStr, params);
    }

    private void buildSortExpression(Pageable pageable, StringBuilder queryStr, boolean allowPrefix) {
        Sort sort = pageable.getSort();
        if (sort != null && !sort.isUnsorted()) {
            queryStr.append(" order by ");
            Iterator<Sort.Order> i = sort.iterator();
            boolean isFirst = true;
            while (i.hasNext()) {
                Sort.Order order = i.next();
                if (isFirst) {
                    isFirst = false;
                } else {
                    queryStr.append(", ");
                }
                String propName = order.getProperty();
                if (allowPrefix) {
                    Prefix prefix = extractPrefix(propName);
                    populatePrefixEntityPropertiesMapIfNull(prefix);
                    propName = removePrefixFromPropName(propName, prefix);
                    if (prefix == null) queryStr.append("e.");
                    else queryStr.append(prefix.getAs());
                } else queryStr.append("e.");
                    queryStr.append(propName).append(" ").append(order.getDirection().name());
            }
        }
    }


    private Page<T> queryEntityPage(Pageable pageable, StringBuilder queryStr, Map<String, Object> params) {
        TypedQuery<Long> countQuery = createLongTypedQuery(getTotalQueryStr(queryStr), params);
        buildSortExpression(pageable, queryStr, false);
        TypedQuery<T> query = createPaginatedTypedQuery(pageable, getEntityQueryStr(queryStr), params);

        return new PageImpl<T>(query.getResultList(), pageable, countQuery.getSingleResult());
    }

    private Page<EntityTask<T>> queryEntityTaskPage(Pageable pageable, StringBuilder queryStr, Map<String, Object> params) {
        TypedQuery<Long> countQuery = createLongTypedQuery(getTotalQueryStr(queryStr), params);
        buildSortExpression(pageable, queryStr, true);
        Query query = createPaginatedQuery(pageable, getEntityTaskQueryStr(queryStr), params);

        return new PageImpl<EntityTask<T>>(query.getResultList(), pageable, countQuery.getSingleResult());
    }

    private TypedQuery<Long> createLongTypedQuery(String queryStr, Map<String, Object> params) {
        logger.info("Query: " + queryStr);
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            logger.info("Params: " + param.getKey() + " | " + param.getValue());
            query.setParameter(param.getKey(), param.getValue());
        }
        return query;
    }

    private TypedQuery<T> createPaginatedTypedQuery(Pageable pageable, String queryStr, Map<String, Object> params) {
        TypedQuery<T> query = createTypedQuery(queryStr, params);
        query.setFirstResult(Math.toIntExact(pageable.getOffset()));
        query.setMaxResults(pageable.getPageSize());
        return query;
    }

    private Query createPaginatedQuery(Pageable pageable, String queryStr, Map<String, Object> params) {
        Query query = createQuery(queryStr, params);
        query.setFirstResult(Math.toIntExact(pageable.getOffset()));
        query.setMaxResults(pageable.getPageSize());
        return query;
    }

    private TypedQuery<T> createTypedQuery(String queryStr, Map<String, Object> params) {
        logger.info("Query: " + queryStr);
        TypedQuery<T> query = entityManager.createQuery(queryStr, getDomainClass());
        for (Map.Entry<String, Object> param : params.entrySet()) {
            logger.info("Params: " + param.getKey() + " | " + param.getValue());
            query.setParameter(param.getKey(), param.getValue());
        }
        return query;
    }

    @Override
    public List<T> simpleSearch(Map<String, Object> searchParamMap) {
        logger.info("Simple Search");
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityQueryFromStr(queryStr);
        Map<String, Object> params = new HashMap<>();
        buildSimpleQueryFilterStr(queryStr, params, searchParamMap, false);

        TypedQuery<T> query = createTypedQuery(getEntityQueryStr(queryStr), params);

        return query.getResultList();
    }

    @Override
    public Page<T> simpleSearch(Pageable pageable, Map<String, Object> searchParamMap) {
        logger.info("Simple Search Pageable");
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityQueryFromStr(queryStr);
        Map<String, Object> params = new HashMap<>();
        buildSimpleQueryFilterStr(queryStr, params, searchParamMap, false);

        return queryEntityPage(pageable, queryStr, params);
    }

    private String getEntityTaskQueryStr(StringBuilder fromQueryStr) {
        return "select new com.demo.test.common.workflow.dto.EntityTask(e, t) " + fromQueryStr;
    }

    private String getEntityQueryStr(StringBuilder fromQueryStr) {
        return "select e " + fromQueryStr;
    }

    private String getTotalQueryStr(StringBuilder fromQueryStr) {
        return "select count(e) " + fromQueryStr;
    }
    private void buildEntityQueryFromStr(StringBuilder queryStr) {
        queryStr.append(" from ").append(getDomainClass().getSimpleName()).append(" e where 1=1");
    }

    private void buildEntityTaskQueryFromStr(StringBuilder queryStr) {
        queryStr.append(" from Task t join fetch ").append(getDomainClass().getSimpleName()).append(" e on t.processBusinessKey = e.id where 1=1");
    }


    public List<EntityTask<T>> simpleSearchTaskByUserRights(String userId, Collection<String> userRoles, Map<String, Object> searchMap) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildSimpleQueryFilterStr(queryStr, params, searchMap, true);

        buildQueryUserRightsFilterStr(userId, userRoles, queryStr, params);

        Query query = createQuery(getEntityTaskQueryStr(queryStr), params);
        return query.getResultList();
    }

    public Page<EntityTask<T>> simpleSearchTaskByUserRights(Pageable pageable, String userId, Collection<String> userRoles, Map<String, Object> searchMap) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildSimpleQueryFilterStr(queryStr, params, searchMap, true);

        buildQueryUserRightsFilterStr(userId, userRoles, queryStr, params);

        return queryEntityTaskPage(pageable, queryStr, params);
    }

    public List<EntityTask<T>> searchTaskByUserRights(String userId, Collection<String> userRoles, Object searchDto) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildQueryFilterStr(queryStr, params, searchDto, "", true);

        buildQueryUserRightsFilterStr(userId, userRoles, queryStr, params);

        Query query = createQuery(getEntityTaskQueryStr(queryStr), params);
        return query.getResultList();
    }

    public Page<EntityTask<T>> searchTaskByUserRights(Pageable pageable, String userId, Collection<String> userRoles, Object searchDto) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildQueryFilterStr(queryStr, params, searchDto, "", true);

        buildQueryUserRightsFilterStr(userId, userRoles, queryStr, params);

        return queryEntityTaskPage(pageable, queryStr, params);
    }

    private void buildQueryUserRightsFilterStr(String userId, Collection<String> userRoles, StringBuilder queryStr, Map<String, Object> params) {
        queryStr.append(" AND (t.assignee = :_userId OR :_userId MEMBER OF t.candidateUsers");
        params.put("_userId", userId);
        if (userRoles != null && userRoles.size() > 0) {
            queryStr.append(" OR (");
            int index = 0;
            for (String role : userRoles) {
                if (index != 0) {
                    queryStr.append(" AND ");
                    index++;
                }
                queryStr.append(":_role" + index + " MEMBER OF t.candidateGroups");
                params.put("_role" + index, role);
            }
            queryStr.append(")");
        }
        queryStr.append(")");
    }

    @Override
    public List<EntityTask<T>> simpleSearchTask(Map<String, Object> searchBy) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildSimpleQueryFilterStr(queryStr, params, searchBy, true);

        Query query = createQuery(getEntityTaskQueryStr(queryStr), params);
        return query.getResultList();
    }

    @Override
    public Page<EntityTask<T>> simpleSearchTask(Pageable pageable, Map<String, Object> searchBy) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildSimpleQueryFilterStr(queryStr, params, searchBy, true);

        return queryEntityTaskPage(pageable, queryStr, params);
    }

    @Override
    public List<EntityTask<T>> searchTask(Object searchDto) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildQueryFilterStr(queryStr, params, searchDto, "", true);

        Query query = createQuery(getEntityTaskQueryStr(queryStr), params);
        return query.getResultList();
    }

    @Override
    public Page<EntityTask<T>> searchTask(Pageable pageable, Object searchDto) {
        populateEntityPropertiesMapIfNull(getDomainClass());

        StringBuilder queryStr = new StringBuilder();
        buildEntityTaskQueryFromStr(queryStr);

        Map<String, Object> params = new HashMap<>();
        buildQueryFilterStr(queryStr, params, searchDto, "", true);

        return queryEntityTaskPage(pageable, queryStr, params);
    }

    private Query createQuery(String queryStr, Map<String, Object> params) {
        logger.info("Query: " + queryStr);
        Query query = entityManager.createQuery(queryStr);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            logger.info("Params: " + param.getKey() + " | " + param.getValue());
            query.setParameter(param.getKey(), param.getValue());
        }
        return query;
    }

    private enum Prefix {
        WORKFLOW_TASK("task", Task.class, "t");

        private final String prefix;
        private final Class prefixEntityClass;
        private final String as;
        private final Pattern regexPattern;

        Prefix(String prefix, Class prefixEntityClass, String as) {
            this.prefix = prefix;
            this.prefixEntityClass = prefixEntityClass;
            this.regexPattern = Pattern.compile("^" + prefix);
            this.as = as;
        }

        public String getPrefix() {
            return prefix;
        }

        public Class getPrefixEntityClass() {
            return prefixEntityClass;
        }

        public String getAs() {
            return as;
        }

        public Pattern getRegexPattern() {
            return regexPattern;
        }
    }

    private enum Operator {
        IS_NULL("Inl", "(", " IS NULL AND ", " = TRUE)", false),
        IS_NOT_NULL("Inn", "(", " IS NOT NULL AND ", " = TRUE)", false),
        START_WITH_IGNORE_CASE("Swi", "LOWER(", ") LIKE LOWER(CONCAT(", ", '%'))"),
        END_WITH_IGNORE_CASE("Ewi", "LOWER(", ") LIKE LOWER(CONCAT('%', ", "))"),
        CONTAINS_IGNORE_CASE("Coi", "LOWER(", ") LIKE LOWER(CONCAT('%', ", ", '%'))"),
        EQUALS_IGNORE_CASE("Eqi", "LOWER(", ") = LOWER(", ")"),
        START_WITH("Stw", "", " LIKE CONCAT(", ", '%')"),
        END_WITH("Enw", "", " LIKE CONCAT('%', ", ")"),
        CONTAINS("Con", "", " LIKE CONCAT('%', ", ", '%')"),
        NOT_EQUALS("Neq", "", " != ", ""),
        EQUALS("Eql", "", " = ", ""),
        GREATER_THAN("Grt", "", " > ", ""),
        LESSER_THAN("Lst", "", " < ", ""),
        GREATER_EQUALS("Gte", "", " >= ", ""),
        LESSER_EQUALS("Lte", "", " <= ", ""),
        IN("Inc", "", " IN ", ""),
        NOT_IN("Nin", "", " NOT IN ", ""),
        MEMBER_OF("Meo", "", " MEMBER OF ", "", true),
        NOT_MEMBER_OF("Nmo", "", " NOT MEMBER OF ", "", true),
        IS_EMPTY("Iem", "(", " IS EMPTY AND ", " = TRUE)", false),
        IS_NOT_EMPTY("Ine", "(", " IS NOT EMPTY AND ", " = TRUE)", false),
        BETWEEN("Btw", "", " BETWEEN ", ""),
        NOT_BETWEEN("Nbt", "", " NOT BETWEEN ", "");


        private final String value;
        private final String expPre;
        private final String expMid;
        private final String expPost;
        private final Pattern regexPattern;
        private final boolean swapValueOrder;

        Operator(String value, String expPre, String expMid, String expPost) {
            this.value = value;
            this.expPre = expPre;
            this.expMid = expMid;
            this.expPost = expPost;
            this.regexPattern = Pattern.compile(value + "$");
            this.swapValueOrder = false;
        }

        Operator(String value, String expPre, String expMid, String expPost, boolean swapValueOrder) {
            this.value = value;
            this.expPre = expPre;
            this.expMid = expMid;
            this.expPost = expPost;
            this.regexPattern = Pattern.compile(value + "$");
            this.swapValueOrder = swapValueOrder;
        }

        public String getValue() {
            return value;
        }

        public String getExpression(String entityValue, String searchValue) {
            if (swapValueOrder) return expPre + searchValue + expMid + entityValue + expPost;
            else return expPre + entityValue + expMid + searchValue + expPost;
        }

        public Pattern getRegexPattern() {
            return regexPattern;
        }
    }
}
