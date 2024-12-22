package ie.atu.taskmanagementservice.Tasks;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskDB taskDB;

    public TaskService(TaskDB taskDB) {
        this.taskDB = taskDB;
    }

    public ResponseEntity<List<Task>> getAllTasksForUser(String email) {
        Optional<List<Task>> getAllTasks = taskDB.findAllByEmail(email);
        if (getAllTasks.isPresent()) {
            List<Task> allTasks = getAllTasks.get();
            return ResponseEntity.ok(allTasks);
        } else {
            List<Task> empty = new ArrayList<>();
            return ResponseEntity.status(404).body(empty);
        }
    }

    public ResponseEntity<String> createTask(Task task) {
        taskDB.save(task);
        return ResponseEntity.ok("Task created: " + task.getTitle());
    }

    public ResponseEntity<String> updateTask(String id, String email, Task task) {
        Optional<Task> oldTask = taskDB.findById(id);
        if (taskDB.existsById(id) && oldTask.isPresent() && Objects.equals(oldTask.get().getEmail(), email)) {
            oldTask.get().setTitle(task.getTitle());
            oldTask.get().setDescription(task.getDescription());
            oldTask.get().setStatus(task.getDescription());
            taskDB.save(oldTask.get());
            return ResponseEntity.ok("Task updated: " + task.getTitle());
        } else {
            return ResponseEntity.status(404).body("Task not found.");
        }
    }

    public ResponseEntity<String> deleteTask(String id, String email) {
        Optional<Task> task = taskDB.findById(id);
        if (taskDB.existsById(id) && task.isPresent() && Objects.equals(task.get().getEmail(), email)) {
            taskDB.deleteById(id);
            return ResponseEntity.ok("Task deleted successfully: " + task.get().getTitle());
        } else {
            return ResponseEntity.status(404).body("Task not found.");
        }
    }

}
