package com.demo.test.common.service;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AutoServiceBeanFactory implements BeanFactoryPostProcessor {
    Logger logger = LoggerFactory.getLogger(AutoServiceBeanFactory.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        logger.info("AutoServiceBeanFactory.postProcessBeanFactory");
        defineAutoServices(configurableListableBeanFactory);
    }

    private void defineAutoServices(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        logger.info("Defining Auto Services (Proxy to Interface)");

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        provider.addIncludeFilter(new AnnotationTypeFilter(AutoService.class));

        final Pattern pattern = Pattern.compile("([^.]+$)");
        for (BeanDefinition candidate : provider.findCandidateComponents("com.demo.test")) {
            final Matcher matcher = pattern.matcher(candidate.getBeanClassName());

            String beanName;
            if (matcher.find()) {
                beanName = matcher.group(0);
                beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
            } else {
                beanName = "bean" + UUID.randomUUID();
            }

            logger.info("Bean Name: " + beanName);

            LazyInitTargetSource lazyTarget = new LazyInitTargetSource();
            lazyTarget.setTargetBeanName(beanName);
            lazyTarget.setBeanFactory(configurableListableBeanFactory);

            GenericBeanDefinition bfBd = new GenericBeanDefinition();
            bfBd.setAutowireCandidate(true);
            bfBd.setLazyInit(true);
            bfBd.setBeanClass(ProxyFactoryBean.class);
            bfBd.getPropertyValues().add("proxyInterfaces", candidate.getBeanClassName());
            bfBd.getPropertyValues().add("targetSource", lazyTarget);
            ManagedList<String> interceptors = new ManagedList<>();
            interceptors.add(beanName + "Interceptor");
            bfBd.getPropertyValues().add("interceptorNames", interceptors);

            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
            beanFactory.registerBeanDefinition(beanName + "FactoryBean", bfBd);

            GenericBeanDefinition interceptorBd = new GenericBeanDefinition();
            interceptorBd.setBeanClass(AutoServiceInterceptor.class);
            interceptorBd.setLazyInit(true);
            interceptorBd.getPropertyValues().add("modelMapper", new RuntimeBeanReference(ModelMapper.class));
            beanFactory.registerBeanDefinition(beanName + "Interceptor", interceptorBd);

            try {
                GenericBeanDefinition bsBd = new GenericBeanDefinition();
                bsBd.setAutowireCandidate(true);
                bsBd.setLazyInit(true);
                if (BaseAutoWorkflowService.class.isAssignableFrom(Class.forName(candidate.getBeanClassName()))) {
                    bsBd.setBeanClass(BaseAutoWorkflowServiceImpl.class);
                } else {
                    bsBd.setBeanClass(BaseAutoServiceImpl.class);
                }
                AutoService autoSvcAnnotation = null;
                autoSvcAnnotation = Class.forName(candidate.getBeanClassName()).getAnnotation(AutoService.class);
                Class entityRepoClass = autoSvcAnnotation.entityRepoClass();
                Class entityClass = autoSvcAnnotation.entityClass();

                ConstructorArgumentValues cavs = new ConstructorArgumentValues();
                cavs.addIndexedArgumentValue(0, entityClass);
                cavs.addIndexedArgumentValue(1, new RuntimeBeanReference(entityRepoClass));
                bsBd.setConstructorArgumentValues(cavs);
                beanFactory.registerBeanDefinition(beanName, bsBd);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
