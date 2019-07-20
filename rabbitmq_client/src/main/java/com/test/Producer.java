package com.test;

import com.rabbitmq.client.*;

import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Producer {

    private final static String QUEUE_NAME = "series.queue";
    private final static String CUST_QUEUE_NAME = "batch.queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("192.168.10.213");
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("user");
        factory.setVirtualHost("vrm");
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        Channel channel2 = connection.createChannel();

        channel.exchangeDeclare("vrm.test", "direct",true);
        channel2.exchangeDeclare("vrm.test", "direct",true);

        Episode e = new Episode();
        HashMap<String, String> m = new HashMap<String, String>();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel2.queueDeclare(CUST_QUEUE_NAME, true,false,false,null);

        // channel.queueBind(QUEUE_NAME,"vrm.test",QUEUE_NAME);
        // channel2.queueBind(CUST_QUEUE_NAME,"vrm.test",CUST_QUEUE_NAME);

        for (int i = 0; i < 10; i++) {

            m.put("Series", "s"+i);
            e.setName("Episode"+i);
            e.setSeries(m);

            com.rabbitmq.client.AMQP.BasicProperties bs = new com.rabbitmq.client.AMQP.BasicProperties(){
                HashMap m=new HashMap<>();

                @Override
                public String getUserId() {
                    return null;
                }
            
                @Override
                public String getType() {
                    return null;
                }
            
                @Override
                public Date getTimestamp() {
                    return null;
                }
            
                @Override
                public String getReplyTo() {
                    return null;
                }
            
                @Override
                public Integer getPriority() {
                    return null;
                }
            
                @Override
                public String getMessageId() {
                    return null;
                }
            
                @Override
                public Map<String, Object> getHeaders() {
                    return m;
                }
            
                @Override
                public String getExpiration() {
                    return null;
                }
            
                @Override
                public Integer getDeliveryMode() {
                    return null;
                }
            
                @Override
                public String getCorrelationId() {
                    return null;
                }
            
                @Override
                public String getContentType() {
                    return null;
                }
            
                @Override
                public String getContentEncoding() {
                    return null;
                }
            
                @Override
                public String getAppId() {
                    return null;
                }
            };

            bs.getHeaders().put("test123", "test123");
            
            if(i%2==0)
                channel.basicPublish("vrm.test", QUEUE_NAME, bs,
                        SerializationUtils.serialize(e));
            else
                channel.basicPublish("vrm.test", CUST_QUEUE_NAME, bs, SerializationUtils.serialize(e));

            System.out.println(" [x] Sent " + e.getName());
        }

        try {
			Thread.sleep(75000);
		} catch (InterruptedException e1) {

			e1.printStackTrace();
		}

        channel.close();
        connection.close();
    }
}