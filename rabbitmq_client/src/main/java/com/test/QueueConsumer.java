package com.test;

import com.rabbitmq.client.*;

import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import java.util.stream.IntStream;

public class QueueConsumer {


    public static void main (String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("192.168.10.213");
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("user");
        factory.setVirtualHost("vrm");

        Connection connection = factory.newConnection();

        ExecutorService e = Executors.newFixedThreadPool(10);


        IntStream.range(1, 4).forEach(action->{

            Channel channel=null;
            try {
                channel = connection.createChannel();
                channel.basicQos(1);
            } catch (IOException e1) {

                e1.printStackTrace();
            }

            ///ConsumerCallback callback = new ConsumerCallback(channel);
            EpisodeConsumer ep=null;
            if(action%2==0)
                ep = new EpisodeConsumer(channel, "series.queue", action);
            else
                ep = new EpisodeConsumer(channel, "batch.queue", action);
            ep.setService(e);
            e.submit(ep);
    
        });


    }




}

class EpisodeConsumer implements Runnable{
    Channel channel;
//    ConsumerCallback callback;
    String queueName;
    int index;
    ExecutorService service;
    public EpisodeConsumer(Channel channel, String queueName,int index){
        this.channel = channel;
        this.queueName = queueName;
        this.index = index;
    }

    @Override
    public void run() {
        try{

        System.out.println("tag"+index);

        channel.basicConsume(queueName, false, (consumerTag, message)->{
    
            String routingKey =  message.getEnvelope().getRoutingKey();
            String contentType =  message.getProperties().getContentType();

            long deliveryTag =  message.getEnvelope().getDeliveryTag();
            System.out.println("DeliveryTag:"+ deliveryTag);
            System.out.println("ConsumerTag:"+ consumerTag);
            System.out.println("Properties:"+ message.getProperties().getHeaders());
            System.out.println("Routing Key:"+routingKey);
            System.out.println("Content Type:"+contentType);

            Episode e = (Episode) SerializationUtils.deserialize( message.getBody());
            System.out.println(e.getName() );
            channel.basicAck(deliveryTag, false);
        }, 
            (consumerTag,sig) -> {
                System.out.println("Queuename "+this.queueName+ " isInitiatedByApplication "+ sig.isInitiatedByApplication()
                                    + " isHardError "+sig.isHardError());

                System.out.println("Exception Consumer: " + consumerTag +" Signal "+ sig.getMessage());
                Connection connection=null;
                        try {
                            ConnectionFactory factory = new ConnectionFactory();

                            factory.setHost("localhost");
                            factory.setUsername("user");
                            factory.setPassword("user");

                            connection = factory.newConnection();
                            Channel channel = connection.createChannel();

                            channel.queueBind("series.queue","vrm.test","series.queue");

                            channel.basicQos(1);

                            EpisodeConsumer consumer = new EpisodeConsumer(channel, "series.queue", 1);
                            consumer.setService(service);

                            service.submit(consumer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }



                System.out.println("created new");
            }
        );
        
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        
    }

    public ExecutorService getService() {
        return service;
    }

    public void setService(ExecutorService service) {
        this.service = service;
    }
}