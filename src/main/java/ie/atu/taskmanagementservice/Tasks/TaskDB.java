package ie.atu.taskmanagementservice.Tasks;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskDB extends MongoRepository<Task, String> {
    Optional<List<Task>> findAllByEmail(String email);
}
