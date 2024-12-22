package ie.atu.taskmanagementservice.Tasks;

import ie.atu.taskmanagementservice.Config.JwtStore;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    private final TaskDB taskDB;
    private final RabbitTemplate rabbitTemplate;
    private final JwtStore jwtStore;

    public TaskService(TaskDB taskDB, RabbitTemplate rabbitTemplate, JwtStore jwtStore) {
        this.taskDB = taskDB;
        this.rabbitTemplate = rabbitTemplate;
        this.jwtStore = jwtStore;
    }

    public ResponseEntity<List<Task>> getAllTasksForUser(String email, String jwt) {
        Optional<List<Task>> getAllTasks = taskDB.findAllByEmail(email);
        if (Objects.equals(jwt, jwtStore.getJwt())) {
            if (getAllTasks.isPresent()) {
                List<Task> allTasks = getAllTasks.get();
                return ResponseEntity.ok(allTasks);
            } else {
                List<Task> empty = new ArrayList<>();
                return ResponseEntity.status(404).body(empty);
            }
        } else {
            return ResponseEntity.status(406).body(Collections.emptyList());
        }
    }

    public ResponseEntity<String> createTask(Task task, String jwt) {
        if (Objects.equals(jwt, jwtStore.getJwt())) {
            Notification notification = new Notification();
            notification.setActionType("CREATE_TASK");
            notification.setEmail(task.getEmail());
            rabbitTemplate.convertAndSend("createTaskSendNotificationQueue", notification);
            taskDB.save(task);
            return ResponseEntity.ok("Task created: " + task.getTitle());
        } else {
            return ResponseEntity.status(406).body("Incorrect Authorization Header");
        }
    }

    public ResponseEntity<String> updateTask(String id, String email, Task task, String jwt) {
        Optional<Task> oldTask = taskDB.findById(id);
        if (Objects.equals(jwt, jwtStore.getJwt())) {
            Notification notification = new Notification();
            if (taskDB.existsById(id) && oldTask.isPresent() && Objects.equals(oldTask.get().getEmail(), email)) {
                oldTask.get().setTitle(task.getTitle());
                oldTask.get().setDescription(task.getDescription());
                oldTask.get().setStatus(task.getDescription());
                taskDB.save(oldTask.get());
                notification.setActionType("UPDATE_TASK");
                notification.setEmail(task.getEmail());
                rabbitTemplate.convertAndSend("updateTaskSendNotificationQueue", notification);
                return ResponseEntity.ok("Task updated: " + task.getTitle());
            } else {
                return ResponseEntity.status(404).body("Task not found.");
            }
        } else {
            return ResponseEntity.status(406).body("Incorrect Authorization Header");
        }
    }

    public ResponseEntity<String> deleteTask(String id, String email, String jwt) {
        Optional<Task> task = taskDB.findById(id);
        if (Objects.equals(jwt, jwtStore.getJwt())) {
            Notification notification = new Notification();
            if (taskDB.existsById(id) && task.isPresent() && Objects.equals(task.get().getEmail(), email)) {
                taskDB.deleteById(id);
                notification.setActionType("DELETE_TASK");
                notification.setEmail(email);
                rabbitTemplate.convertAndSend("deleteTaskSendNotificationQueue", notification);
                return ResponseEntity.ok("Task deleted successfully: " + task.get().getTitle());
            } else {
                return ResponseEntity.status(404).body("Task not found.");
            }
        } else {
            return ResponseEntity.status(406).body("Incorrect Authorization Header");
        }
    }

    @RabbitListener(queues = {"createTaskRecNotificationQueue", "updateTaskRecNotificationQueue", "deleteTaskRecNotificationQueue"})
    public void receiveNotification(Notification notification) {
        notification.setRead(true);
        System.out.println(notification);
    }

}
