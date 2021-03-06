/**
 * Copyright(C) 2017 Fugle Technology Co. Ltd. All rights reserved.
 *
 */
package com.bob.root.concrete.modifybean;

import com.bob.root.config.entity.RootUser;
import com.bob.root.config.TestContextConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

/**
 * @since 2017年1月23日 上午10:30:05
 * @version $Id$
 * @author JiangJibo
 *
 */
public class ModifyBeanTest extends TestContextConfig {

	private BeanFactory beanFactory;

	@Before
	public void init() {
		beanFactory = this.webApplicationContext.getAutowireCapableBeanFactory();
	}

	@Test
	public void testModify() {
		RootUser user = beanFactory.getBean("user", RootUser.class);
		System.out.println(user.getName());
		user.setName("named by modify");
		RootUser uu = (RootUser) beanFactory.getBean("user");
		System.out.println(uu.getName());
	}

}
