package ie.atu.taskmanagementservice.Tasks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/allTasks/{email}")
    public ResponseEntity<List<Task>> getAllTasksForUser(@PathVariable String email, @RequestHeader("Authorization") String jwt) {
        return taskService.getAllTasksForUser(email, jwt);
    }

    @PostMapping("/createTask")
    public ResponseEntity<String> createTask(@RequestBody Task task, @RequestHeader("Authorization") String jwt) {
        return taskService.createTask(task, jwt);
    }

    @PutMapping("/updateTask/{email}/{id}")
    public ResponseEntity<String> updateTask(@PathVariable String id, @PathVariable String email, @RequestBody Task task, @RequestHeader("Authorization") String jwt) {
        return taskService.updateTask(id, email, task, jwt);
    }

    @DeleteMapping("/deleteTask/{email}/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable String id, @PathVariable String email, @RequestHeader("Authorization") String jwt) {
        return taskService.deleteTask(id, email, jwt);
    }

}
