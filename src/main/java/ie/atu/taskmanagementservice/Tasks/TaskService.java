package ie.atu.taskmanagementservice.Tasks;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskDB taskDB;
    private final RabbitTemplate rabbitTemplate;

    public TaskService(TaskDB taskDB, RabbitTemplate rabbitTemplate) {
        this.taskDB = taskDB;
        this.rabbitTemplate = rabbitTemplate;
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
        Notification notification = new Notification();
        try {
            notification.setActionType("CREATE_TASK");
            notification.setEmail(task.getEmail());
            rabbitTemplate.convertAndSend("createTaskSendNotificationQueue", notification);
            taskDB.save(task);
            return ResponseEntity.ok("Task created: " + task.getTitle());
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body("Error creating task");
        }
    }

    public ResponseEntity<String> updateTask(String id, String email, Task task) {
        Optional<Task> oldTask = taskDB.findById(id);
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
    }

    public ResponseEntity<String> deleteTask(String id, String email) {
        Optional<Task> task = taskDB.findById(id);
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
    }

    @RabbitListener(queues = {"createTaskRecNotificationQueue", "updateTaskRecNotificationQueue", "deleteTaskRecNotificationQueue"})
    public void receiveNotification(Notification notification) {
        notification.setRead(true);
        System.out.println(notification);
    }

}
