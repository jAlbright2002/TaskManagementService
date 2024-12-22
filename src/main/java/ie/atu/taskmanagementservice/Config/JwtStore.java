package ie.atu.taskmanagementservice.Config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class JwtStore {

    private static final AtomicReference<String> jwtToken = new AtomicReference<>();

    @RabbitListener(queues = "jwtQueue")
    public void storeJwt(String token) {
        jwtToken.set(token);
    }

    public String getJwt() {
        return jwtToken.get();
    }

}
