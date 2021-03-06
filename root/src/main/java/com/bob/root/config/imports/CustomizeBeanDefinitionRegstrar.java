package com.bob.root.config.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @since 2017年4月5日 上午9:29:45
 * @version $Id$
 * @author JiangJibo
 *
 */
public class CustomizeBeanDefinitionRegstrar implements BeanDefinitionRegistryPostProcessor {

    final static Logger LOGGER = LoggerFactory.getLogger(CustomizeBeanDefinitionRegstrar.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        LOGGER.debug("当前总共有{}个BeanDefiniton", registry.getBeanDefinitionNames().length);
    }

}
