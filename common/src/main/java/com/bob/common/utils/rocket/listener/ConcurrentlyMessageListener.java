package com.bob.common.utils.rocket.listener;

import java.lang.reflect.Method;
import java.util.List;

import com.bob.common.utils.rocket.ann.RocketListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageClientExt;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

/**
 * 封装{@link RocketListener}标识的方法成一个消息并发消费器
 *
 * @author wb-jjb318191
 * @create 2018-03-20 10:03
 */
public class ConcurrentlyMessageListener extends AbstractMessageListener implements MessageListenerConcurrently {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentlyMessageListener.class);

    public ConcurrentlyMessageListener(Object consumeBean, Method consumeMethod) {
        super(consumeBean, consumeMethod);
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        Object[] args = buildConsumeArguments(msgs, context);
        boolean result;
        Exception ex = null;
        try {
            result = (boolean)ReflectionUtils.invokeMethod(consumeMethod, consumeBean, args);
            if (result) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        } catch (Exception e) {
            ex = e;
        }
        warnWhenConsumeFailed((MessageClientExt)msgs.get(0), ex);
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

}
