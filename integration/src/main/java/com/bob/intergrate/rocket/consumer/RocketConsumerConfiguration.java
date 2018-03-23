package com.bob.intergrate.rocket.consumer;

import com.bob.intergrate.rocket.integrate.ann.RocketListener;
import com.bob.intergrate.rocket.integrate.constant.RocketBeanDefinitionConstant;
import com.google.gson.Gson;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ消费者配置
 *
 * @author wb-jjb318191
 * @create 2018-03-20 16:06
 */
@Configuration
public class RocketConsumerConfiguration {

    private Gson gson = new Gson();

    /**
     * 通过{@link RocketListener}形式定义的Consumer,名称形式以为：
     * consumeMethodName + {@link RocketBeanDefinitionConstant#ROCKETMQ_CONSUMER_BEAN_NAME_SUFFIX}
     * BeanName形式可自定义
     */
    @Autowired
    private DefaultMQPushConsumer serviceRocketConsumer;

    /**
     * 定义RocketMQ消费器
     *
     * @param msg
     * @param context
     * @return true:消费成功;  false:消费失败,发回给Broker,一段时间后重试
     */
    @RocketListener(consumerGroup = "${service.consumerGroup}", topic = "${service.topic}")
    public boolean service(MessageExt msg, ConsumeConcurrentlyContext context) {
        System.out.println(gson.toJson(msg));
        return true;
    }

}