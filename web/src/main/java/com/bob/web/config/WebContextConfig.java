package com.bob.web.config;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;

import com.bob.common.utils.userenv.ann.EnableUserEnv;
import com.bob.common.utils.validate.EnableDataValidate;
import com.bob.integrate.mybatis.MybatisContextConfig;
import com.bob.integrate.mybatis.tx.TransactionContextConfig;
import com.bob.integrate.redis.RedisContextConfig;
import com.bob.web.config.aop.AopContextConfig;
import com.bob.web.config.async.AsyncCallableInterceptor;
import com.bob.web.config.async.AsyncDeferredResultInterceptor;
import com.bob.web.config.exception.DefaultExceptionResolver;
import com.bob.web.config.formatter.String2DateFormatter;
import com.bob.web.config.formatter.StudentFormatter;
import com.bob.web.config.interceptor.LoginInterceptor;
import com.bob.web.config.jwt.SpringBeanInstanceAccessor;
import com.bob.web.config.stringvalueresolver.DefaultStringValueResolver;
import com.bob.web.config.stringvalueresolver.StringValueResolverRegistrar;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import static org.springframework.context.support.AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;

/**
 * @author JiangJibo
 * @version $Id$
 * @since 2016年12月5日 下午4:20:35
 */
@Configuration
@EnableAsync
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"com.bob.web.mvc"})
@Import({
    MybatisContextConfig.class,
    TransactionContextConfig.class,
    RedisContextConfig.class,
    AopContextConfig.class,
})
@EnableUserEnv
@EnableDataValidate
//@EnableDubboConfig(application = PROVIDER)
public class WebContextConfig extends WebMvcConfigurerAdapter {

    final static Logger LOGGER = LoggerFactory.getLogger(WebContextConfig.class);

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 为{@link EventListener}事件监听设置线程池,使其支持异步执行
     */
    @PostConstruct
    public void init() {
        SimpleApplicationEventMulticaster multicaster = beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, SimpleApplicationEventMulticaster.class);
        multicaster.setTaskExecutor(threadPoolTaskExecutor);
    }

    @Bean
    public DefaultStringValueResolver defaultStringValueResolver() {
        return new DefaultStringValueResolver();
    }

    /**
     * 设置此方法为静态的意义是不要让当前类在BeanPostProcessor实例化时就触发实例化,也就是解耦这两个Bean的依赖关系
     * 否则{@link AbstractApplicationContext#APPLICATION_EVENT_MULTICASTER_BEAN_NAME}名称的Bean还未被注册
     * 当前配置类通过getBean()就获取不到事件广播器,也就不能为其设置执行线程池
     *
     * @return
     */
    @Bean
    public static StringValueResolverRegistrar stringValueResolverRegister() {
        return new StringValueResolverRegistrar();
    }

    @Bean
    public SpringBeanInstanceAccessor defaultBeanFactoryUtils() {
        return new SpringBeanInstanceAccessor();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    /**
     * 定义文件上传的处理器
     *
     * @return
     */
    @Bean("multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(10 * 1024 * 1024);
        return multipartResolver;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        //设置Date类型使用HttpMessageConverter转换后的格式,或者注册一个GsonHttpMessageConverter,能直接支持字符串到日期的转换
        //当指定了日期字符串格式后,如果传的日志格式不符合,则会解析错误
        converters.add(new MappingJackson2HttpMessageConverter(
            new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))));
        //GsonHttpMessageConverter不支持yyyy-MM-dd形式的字符串转换为日期
        //GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
        //gsonConverter.setGson(GsonGenerator.newGsonInstance());
        //converters.add(new GsonHttpMessageConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.useJaf(false).favorPathExtension(false).favorParameter(true).parameterName("mediaType")
            .ignoreAcceptHeader(true).defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //exposedHeaders设置Header可拓展的参数名称,比如希望用将token参数用Header传递,需要设置exposedHeaders("token")
        registry.addMapping("/**").allowedOrigins("*").allowCredentials(true)
            .allowedMethods("GET", "POST", "DELETE", "PUT").maxAge(3600).exposedHeaders("token");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor intcep = new LoginInterceptor();
        registry.addInterceptor(new MappedInterceptor(intcep.getIncludePatterns(), intcep.getExcludePatterns(), intcep));
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        ReloadableResourceBundleMessageSource messageResource = new ReloadableResourceBundleMessageSource();
        messageResource.setBasenames("classpath:com/bob/validation/ValidationMessages");
        validator.setValidationMessageSource(messageResource);
        return validator;
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new DefaultExceptionResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new String2DateFormatter());
        registry.addFormatter(new StudentFormatter());
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(5 * 1000);
        configurer.setTaskExecutor(threadPoolTaskExecutor);
        configurer.registerCallableInterceptors(new AsyncCallableInterceptor());
        configurer.registerDeferredResultInterceptors(new AsyncDeferredResultInterceptor());
    }

}
