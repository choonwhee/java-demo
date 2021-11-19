package com.demo.test.common.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

public class AutoServiceInterceptor implements MethodInterceptor {
    Logger logger = LoggerFactory.getLogger(AutoServiceInterceptor.class);

    private ModelMapper modelMapper;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method invMethod = invocation.getMethod();

        Object[] invArguments = invocation.getArguments();
        Class returnType = invMethod.getReturnType();
        Class invocationClass = invocation.getThis().getClass();
        String invocationClassName = invocationClass.getName();
        Class invMethodDeclaringClass = invMethod.getDeclaringClass();
        String invMethodDeclaringClassName = invMethodDeclaringClass.getName();
        boolean invMethodDeclaringClassIsInterface = invMethodDeclaringClass.isInterface();
        boolean invMethodDefinedByAutoService = (invMethodDeclaringClass.isAnnotationPresent(AutoService.class));
        Class[] paramTypes = invMethod.getParameterTypes();
        logger.info("-------------------------------------");
        logger.info("AutoServiceInterceptor Method Invoked ");
        logger.info("Invocation Class: " + invocationClassName);
        logger.info("Inv Method Declaring Class: " + invMethodDeclaringClassName);
        logger.info("Inv Method Declaring Class is Interface: " + invMethodDeclaringClassIsInterface);
        logger.info("Inv Method defined by AutoService: " + invMethodDefinedByAutoService);
        logger.info("Inv Method Name: " + invMethod.getName());
        for (Class paramType : paramTypes) logger.info("Inv Method Parameter Type: " + paramType.getName());
        logger.info("Inv Method Return Type: " + returnType);
        logger.info("Inv Method Arguments: " + invArguments);
        logger.info("-------------------------------------");

        if (invMethodDefinedByAutoService && !hasMethodImpl(invocationClass, invMethod)) {
            Object repo = getServiceEntityRepo(invocation);
            logger.info("ServiceEntityRepo: " + repo);
            logger.info("serviceMethodName: " + invMethod.getName());

            String methodName = invMethod.getName();
            final String saveMethodName = "save";
            final String saveDtoAndStartProcessMethodName = "saveDtoAndStartProcess";
            final String saveDtoMethodName = "saveDto";
            final String updateMethodName = "update";
            final String updateDtoTaskCompletedMethodName = "updateDtoTaskCompleted";
            final String updateDtoMethodName = "updateDto";
            final String simpleSearchMethodName = "simpleSearch";
            final String simpleSearchTaskByUserRightsMethodName = "simpleSearchTaskByUserRights";
            final String simpleSearchTaskMethodName = "simpleSearchTask";
            final String updateTaskMethodNamePrefix = "updateTask";
            final String updateTaskCompletedMethodName = "updateTaskCompleted";
            final String taskMethodNamePrefix = "task";
            final String taskCompletedMethodName = "taskCompleted";

            if (invMethod.getName().startsWith(saveDtoAndStartProcessMethodName)) {
                return invokeServiceMethodByMethodNameAndParamTypes(saveDtoAndStartProcessMethodName, invocation);
            } else if (invMethod.getName().startsWith(saveDtoMethodName)) {
                return invokeServiceMethodByMethodName(saveDtoMethodName, invocation);
            } else if (invMethod.getName().startsWith(saveMethodName)) {
                return invokeRepoMethodByMethodName(saveMethodName, invocation, repo);
            } else if (invMethod.getName().startsWith(updateDtoTaskCompletedMethodName)) {
                return invokeServiceMethodByMethodNameAndParamTypes(updateDtoTaskCompletedMethodName, invocation);
            } else if (invMethod.getName().startsWith(updateDtoMethodName)) {
                return invokeServiceMethodByMethodName(updateDtoMethodName, invocation);
            } else if (invMethod.getName().equals(updateMethodName)) {
                return invokeServiceMethodByMethodName(updateMethodName, invocation);
            }


            Method matchingRepoMethod = getMatchingRepoMethod(invocation, repo);
            if (matchingRepoMethod != null) {
                logger.info("Invoke Matched Repo Method");
                return invokeMatchingRepoMethod(invocation, repo, matchingRepoMethod);
            } else if (invMethod.getName().startsWith(simpleSearchTaskByUserRightsMethodName)) {
                logger.info("matched invMethod start with:" + simpleSearchTaskByUserRightsMethodName);
                return invokeSimpleSearchByMatchingNameAndReturnType(simpleSearchTaskByUserRightsMethodName, invocation, repo, 2, 3);
            } else if (invMethod.getName().startsWith(simpleSearchTaskMethodName)) {
                logger.info("matched invMethod start with:" + simpleSearchTaskMethodName);
                return invokeSimpleSearchByMatchingNameAndReturnType(simpleSearchTaskMethodName, invocation, repo, 0, 1);
            } else if (invMethod.getName().equals(simpleSearchMethodName)) {
                logger.info("matched invMethod equals:" + simpleSearchMethodName);
                return invokeSimpleSearchByMatchingExactNameAndReturnType(simpleSearchMethodName, invocation, repo);
            } else if (invMethod.getName().startsWith(simpleSearchMethodName)) {
                logger.info("matched invMethod start with:" + simpleSearchMethodName);
                return invokeSimpleSearchByMatchingNameAndReturnType(simpleSearchMethodName, invocation, repo, 0, 1);
            } else if (methodName.startsWith(updateTaskMethodNamePrefix)) {
                logger.info("matched invMethod name start with: " + updateTaskMethodNamePrefix);
                return invokeTaskCompleted(methodName, updateTaskCompletedMethodName, updateTaskMethodNamePrefix, invocation);
            } else if (methodName.startsWith(taskMethodNamePrefix)) {
                logger.info("matched invMethod name start with: " + taskMethodNamePrefix);
                return invokeTaskCompleted(methodName, taskCompletedMethodName, taskMethodNamePrefix, invocation);
            } else {
                logger.info("Invalid AutoService invMethod: could not match invMethod signature to any valid implementation");
            }
        } else {
            logger.info("AutoServiceInterceptor invMethod invocation" + invMethod.getName());
            return invocation.proceed();
        }
        return null;
    }

    private Object invokeServiceMethodByMethodNameAndParamTypes(String methodName, MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        Method svcMethod = getSvcMethodByNameAndParamTypes(methodName, invocation);
        Object[] args = invocation.getArguments();
        Object result = svcMethod.invoke(invocation.getThis(), args);

        return mapResultsIfReturnTypeNotMatching(result, invocation.getMethod());
    }

    private Object invokeServiceMethodByMethodName(String methodName, MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        Method svcMethod = getSvcMethodByName(methodName, invocation);
        Object[] args = invocation.getArguments();
        Object result = svcMethod.invoke(invocation.getThis(), args);

        return mapResultsIfReturnTypeNotMatching(result, invocation.getMethod());
    }

    private Object invokeRepoMethodByMethodName(String methodName, MethodInvocation invocation, Object repo) throws IllegalAccessException, InvocationTargetException {
        Method repoMethod = getRepoMethodByName(methodName, repo);
        Object result = repoMethod.invoke(repo, invocation.getArguments());

        return mapResultsIfReturnTypeNotMatching(result, invocation.getMethod());
    }

    private Object invokeTaskCompleted(String methodName, String targetMethodName, String methodNamePrefix, MethodInvocation invocation) {
        String status = Character.toLowerCase(methodName.charAt(methodNamePrefix.length())) + methodName.substring(methodNamePrefix.length() + 1);
        logger.info("Task Complete Status: " + status);
        Class[] svcInterfaces = invocation.getThis().getClass().getInterfaces();
        for (Class svcInterface : svcInterfaces) {
            try {
                List<Class> targetParamTypes = new ArrayList<>();
                targetParamTypes.add(String.class);
                for (Class paramType : invocation.getMethod().getParameterTypes()) {
                    targetParamTypes.add(paramType);
                }
                Method invokeMethod = svcInterface.getMethod(targetMethodName, targetParamTypes.toArray(new Class[targetParamTypes.size()]));
                List<Object> targetArgs = new ArrayList<>();
                targetArgs.add(status);
                for (Object arg : invocation.getArguments()) {
                    targetArgs.add(arg);
                }
                Object result = invokeMethod.invoke(invocation.getThis(), targetArgs.toArray());
                return mapResultsIfReturnTypeNotMatching(result, invocation.getMethod());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Target Method Not Found: " + targetMethodName);
    }

    private Object invokeSimpleSearchByMatchingNameAndReturnType(String matchedRepoMethodName, MethodInvocation invocation, Object repo, int searchMapParamPositionNonPageable, int searchMapParamPositionPageable) throws IllegalAccessException, InvocationTargetException {
        Parameter[] params = invocation.getMethod().getParameters();
        Object[] args = invocation.getArguments();
        Map<String, Object> searchMap = new HashMap<>();
        boolean isPageable = Page.class.isAssignableFrom(invocation.getMethod().getReturnType());
        int searchMapParamPosition = isPageable ? searchMapParamPositionPageable : searchMapParamPositionNonPageable;
        for (int i = searchMapParamPosition; i < params.length; i++) {
            logger.info("Search Map Param: " + params[i].getName() + "|" + args[i]);
            searchMap.put(params[i].getName(), args[i]);
        }
        List<Object> repoMethodArgs = new ArrayList<>();
        for (int i = 0; i < searchMapParamPosition; i++) {
            repoMethodArgs.add(args[i]);
        }
        repoMethodArgs.add(searchMap);
        logger.info("Repo Method Arg: " + repoMethodArgs);
        Method repoMethod = getRepoMethodByNameAndReturnType(matchedRepoMethodName, repo, isPageable);
        logger.info("repoMethod: " + repoMethod.getReturnType().getName() + " " + repoMethod.getName() + "(" + repoMethod.getParameterTypes() + ")");
        Object result = repoMethod.invoke(repo, repoMethodArgs.toArray());
        return mapResultsIfReturnTypeNotMatching(result, invocation.getMethod());
    }

    private Object invokeSimpleSearchByMatchingExactNameAndReturnType(String matchedRepoMethodName, MethodInvocation invocation, Object repo) throws IllegalAccessException, InvocationTargetException {
        boolean isPageable = Page.class.isAssignableFrom(invocation.getMethod().getReturnType());

        Method repoMethod = getRepoMethodByNameAndReturnType(matchedRepoMethodName, repo, isPageable);
        logger.info("repoMethod: " + repoMethod.getReturnType().getName() + " " + repoMethod.getName() + "(" + repoMethod.getParameterTypes() + ")");
        Object result = repoMethod.invoke(repo, invocation.getArguments());
        return mapResultsIfReturnTypeNotMatching(result, invocation.getMethod());
    }

    private Object mapResultsIfReturnTypeNotMatching(Object result, Method invMethod) {
        if (result == null) return null;
        if (result instanceof Collection || result instanceof Page) {
            Collection resultCollection;
            boolean resultIsPage;
            if (result instanceof Page) {
                resultCollection = ((Page) result).getContent();
                resultIsPage = true;
            } else {
                resultCollection = (Collection) result;
                resultIsPage = false;
            }
            if (Collection.class.isAssignableFrom(invMethod.getReturnType()) || Page.class.isAssignableFrom(invMethod.getReturnType())) {
                if (Page.class.isAssignableFrom(invMethod.getReturnType()) && !resultIsPage) throw new RuntimeException("Expected result and service return type not matching. Result is a Page but service return type is not.");
                if (!Page.class.isAssignableFrom(invMethod.getReturnType()) && resultIsPage) throw new RuntimeException("Expected result and service return type not matching. Service return type is a Page but result is not.");
                if (resultCollection.size() > 0) {
                    ParameterizedType invReturnParameterizedType = (ParameterizedType) invMethod.getGenericReturnType();
                    Type[] invReturnCollectionGenericTypes = invReturnParameterizedType.getActualTypeArguments();
                    if (invReturnCollectionGenericTypes.length > 0) {
                        Type invReturnCollectionGenericType = invReturnCollectionGenericTypes[0];
                        Class invReturnCollectionGenericClass = getClassFromType(invReturnCollectionGenericType);

                        Object firstResult = resultCollection.iterator().next();
                        Class repoResultCollectionObjectClass = firstResult.getClass();
                        logger.info("Repo Result Collection Generic Type: " + repoResultCollectionObjectClass.getName());
                        if (invReturnCollectionGenericClass.isAssignableFrom(repoResultCollectionObjectClass)) {
                            logger.info("Return Collection Generic Type is superclass of Invocation Collection Generic Type, no mapping required.");
                            return result;
                        } else {
                            logger.info("Map Entity Model to DTO");
                            modelMapper.getConfiguration().addValueReader(new IntrospectionValueReader());
                            if (resultIsPage) {
                                List<Object> dtoList = new ArrayList<>();
                                for (Object o : resultCollection) {
                                     dtoList.add(modelMapper.map(o, invReturnCollectionGenericType));
                                }

                                Page resultPage = (Page) result;
                                return new PageImpl(dtoList, resultPage.getPageable(), resultPage.getTotalElements());
                            } else {
                                Object dtoList = modelMapper.map(resultCollection, invReturnParameterizedType);
                                return dtoList;
                            }
                        }
                    } else {
                        //don't map if collection does not have a generic type
                        return result;
                    }
                } else {
                    //return empty collection as is if collection is empty
                    return result;
                }
            } else {
                //service return type is not collection map first result
                if (resultCollection.size() > 0) {
                    if (resultCollection.size() > 1) logger.warn("More than 1 result found when mapping result collection to single return type.");
                    Type invReturnType = invMethod.getGenericReturnType();
                    Class invReturnClass = getClassFromType(invReturnType);

                    Object firstResult = resultCollection.iterator().next();
                    Class repoResultCollectionObjectClass = firstResult.getClass();
                    logger.info("Repo Result Collection Generic Type: " + repoResultCollectionObjectClass.getName());
                    if (invReturnClass.isAssignableFrom(repoResultCollectionObjectClass)) {
                        logger.info("Return Collection Object Class is superclass of Invocation Collection Generic Type, no mapping required.");
                        return firstResult;
                    } else {
                        logger.info("Map Entity Model to DTO");
                        modelMapper.getConfiguration().addValueReader(new IntrospectionValueReader());

                        Object dtoList = modelMapper.map(firstResult, invReturnType);
                        return dtoList;
                    }
                } else {
                    //if repo result collection is empty return null
                    return null;
                }
            }
        } else {
            //mapping single object repo result to single service return type
            Type invReturnType = invMethod.getGenericReturnType();
            Class invReturnClass = getClassFromType(invReturnType);

            Class repoResultObjectClass = result.getClass();
            logger.info("Repo Result Class: " + repoResultObjectClass.getName());
            if (invReturnClass.isAssignableFrom(repoResultObjectClass)) {
                logger.info("Return Object is superclass of Invocation Generic Type, no mapping required.");
                return result;
            } else {
                logger.info("Map Entity Model to DTO");
                modelMapper.getConfiguration().addValueReader(new IntrospectionValueReader());

                Object dtoList = modelMapper.map(result, invReturnType);
                return dtoList;
            }
        }
    }

    private Class getClassFromType(Type invReturnCollectionGenericType) {
        Class invReturnCollectionGenericClass;
        if (invReturnCollectionGenericType instanceof ParameterizedType) {
            logger.info("Is Parameterised Type: " + invReturnCollectionGenericType.getTypeName());
            invReturnCollectionGenericClass = (Class) ((ParameterizedType) invReturnCollectionGenericType).getRawType();
            logger.info("Parameterised Type Class: " + invReturnCollectionGenericClass.getName());

        } else {
            logger.info("Is Class Type: " + invReturnCollectionGenericType);
            invReturnCollectionGenericClass = (Class) invReturnCollectionGenericType;
        }
        return invReturnCollectionGenericClass;
    }

    private Object invokeMatchingRepoMethod (MethodInvocation invocation, Object repo, Method repoMethod) {
        try {
            Object[] args = invocation.getArguments();
            logger.info("invoke:" + repo.getClass() + " | " + repoMethod);
            Object result;
            if(Proxy.isProxyClass(repo.getClass())) {
                InvocationHandler ih = Proxy.getInvocationHandler(repo);
                logger.info("Invocation handler:" + ih);
                result = ih.invoke(repo, repoMethod, args);
                logger.info("Invoke Result: " + result);
            } else {
                result = repoMethod.invoke(repo, args[0]);
            }
            return mapResultsIfReturnTypeNotMatching(result, invocation.getMethod());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Method getSvcMethodByNameAndParamTypes(String invMethodName, MethodInvocation invocation) {
        Class invClass = invocation.getThis().getClass();
        Class[] svcParamTypes = invocation.getMethod().getParameterTypes();
        Class[] invInterfaces = invClass.getInterfaces();
        for (Class invInterface : invInterfaces) {
            for (Method m : invInterface.getMethods()) {
                if (m.getName().equals(invMethodName)) {
                    if (matchMethodParams(svcParamTypes, m)) return m;
                }
            }
        }
        return null;
    }

    private boolean matchMethodParams(Class[] svcParamTypes, Method m) {
        Class[] mParamTypes = m.getParameterTypes();
        if (svcParamTypes.length == mParamTypes.length) {
            boolean allParamsMatch = true;
            for (int i = 0; i < svcParamTypes.length; i++) {
                Class svcParamType = svcParamTypes[0];
                Class mParamType = mParamTypes[0];
                logger.info("Param type " + svcParamType.getSimpleName() + " | " + mParamType.getSimpleName());
                if (!svcParamType.equals(mParamType) && !mParamType.isAssignableFrom(svcParamType)) {
                    allParamsMatch = false;
                    break;
                } else {
                    logger.info("Param " + i + " matched: " + svcParamType.getSimpleName() + " | " + mParamType.getSimpleName());
                }
            }
            if (allParamsMatch) return true;
        }
        return false;
    }

    private Method getSvcMethodByName(String invMethodName, MethodInvocation invocation) {
        Class invClass = invocation.getThis().getClass();
        Class[] invInterfaces = invClass.getInterfaces();
        for (Class invInterface : invInterfaces) {
            for (Method m : invInterface.getMethods()) {
                logger.info(m.getName());
                if (m.getName().equals(invMethodName)) {
                    logger.info("matched " + m.getName());
                    return m;
                }
            }
        }
        return null;
    }

    private Method getRepoMethodByName(String repoMethodName, Object repo) {
        Class repoClass = repo.getClass();
        Class[] repoInterfaces = repoClass.getInterfaces();
        for (Class repoInterface : repoInterfaces) {
            for (Method m : repoInterface.getMethods()) {
                if (m.getName().equals(repoMethodName)) {
                    return m;
                }
            }
        }
        return null;
    }

    private Method getRepoMethodByNameAndReturnType(String repoMethodName, Object repo, boolean isPageable) {
        Class repoClass = repo.getClass();
        Class[] repoInterfaces = repoClass.getInterfaces();
        for (Class repoInterface : repoInterfaces) {
            for (Method m : repoInterface.getMethods()) {
                if (m.getName().equals(repoMethodName)) {
                    if (!isPageable && !Page.class.isAssignableFrom(m.getReturnType())) return m;
                    else if (isPageable && Page.class.isAssignableFrom(m.getReturnType())) return m;
                }
            }
        }
        return null;
    }

    private Method getMatchingRepoMethod(MethodInvocation invocation, Object repo) {
        Method serviceMethod = invocation.getMethod();
        logger.info("Svc Method Name: " + serviceMethod.getName() + " | paramTypes: " + serviceMethod.getParameterTypes().toString());
        logger.info("Repo Class: " + AopUtils.getTargetClass(repo));
        Method matchedRepoMethod = null;

        Class repoClass = repo.getClass();
        Class[] repoInterfaces = repoClass.getInterfaces();
        Class[] svcParamTypes = serviceMethod.getParameterTypes();
        for (Class repoInterface : repoInterfaces) {
            for (Method m : repoInterface.getMethods()) {
                if (serviceMethod.getName().equals(m.getName())) {
                    logger.info("MethodName matched: " + m.getName());
                    if (matchMethodParams(svcParamTypes, m)) return m;
                }
            }
        }
        return null;
    }

    private Object getServiceEntityRepo(MethodInvocation invocation) {
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(invocation.getThis().getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if ("repo".equals(pd.getName())) {
                try {
                    return pd.getReadMethod().invoke(invocation.getThis());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    private boolean hasMethodImpl(Class clazz, Method method) {
        logger.info("has Method Impl: " + clazz + " | " + method);
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            Class<?>[] mParameterTypes = m.getParameterTypes();
            Class<?>[] methodParameterTypes = method.getParameterTypes();
            int mParameterCount = m.getParameterCount();
            if (m.getName().equals(method.getName()) && mParameterCount == method.getParameterCount()) {
                logger.info("Method Name and Param Count matches");
                boolean paramMatch = true;
                for (int j = 0; j < mParameterCount; j++) {
                    if (!mParameterTypes[j].equals(methodParameterTypes[j])) {
                        paramMatch = false;
                        break;
                    }
                }
                logger.info("Params match: " + paramMatch);

                if (paramMatch) {
                    return true;
                }
            }
        }


        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            boolean hasMethodImpl = hasMethodImpl(superClass, method);
            return hasMethodImpl == true;
        }
        return false;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
