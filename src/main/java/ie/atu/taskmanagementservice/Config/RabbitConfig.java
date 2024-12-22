package ie.atu.taskmanagementservice.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public Queue createTaskRecNotificationQueue() {
        return new Queue("createTaskRecNotificationQueue", false);
    }

    @Bean
    public Queue createTaskSendNotificationQueue() {
        return new Queue("createTaskSendNotificationQueue", false);
    }

    @Bean
    public Queue updateTaskRecNotificationQueue() {
        return new Queue("updateTaskRecNotificationQueue", false);
    }

    @Bean
    public Queue updateTaskSendNotificationQueue() {
        return new Queue("updateTaskSendNotificationQueue", false);
    }

    @Bean
    public Queue deleteTaskRecNotificationQueue() {
        return new Queue("deleteTaskRecNotificationQueue", false);
    }

    @Bean
    public Queue deleteTaskSendNotificationQueue() {
        return new Queue("deleteTaskSendNotificationQueue", false);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}