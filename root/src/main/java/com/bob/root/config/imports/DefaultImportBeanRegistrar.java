package com.bob.root.config.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @since 2017年1月19日 上午9:11:49
 * @version $Id$
 * @author JiangJibo
 *
 */
public class DefaultImportBeanRegistrar implements ImportBeanDefinitionRegistrar {

    final static Logger LOGGER = LoggerFactory.getLogger(DefaultImportBeanRegistrar.class);


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(ImportBeanAnnotation.class.getName()));
        AnnotatedGenericBeanDefinition bdf = new AnnotatedGenericBeanDefinition(ImportedBean.class);
        MutablePropertyValues mpv = new MutablePropertyValues();
        mpv.add("id", annAttr.getNumber("id").intValue());
        mpv.add("name", annAttr.getString("value"));
        mpv.add("age", annAttr.getNumber("age").intValue());
        mpv.add("adress", annAttr.getString("adress"));
        mpv.add("telephone", annAttr.getString("telephone"));
        bdf.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
        bdf.setPropertyValues(mpv);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(bdf);
        registry.registerBeanDefinition("importedBean", bdf);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Regisity BeanDefinition [" + bdf.getBeanClassName() + "] by ImportBeanDefinitionRegistrar");
        }
    }

}
