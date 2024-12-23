package ie.atu.taskmanagementservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.atu.taskmanagementservice.Tasks.Task;
import ie.atu.taskmanagementservice.Tasks.TaskController;
import ie.atu.taskmanagementservice.Tasks.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllTasksByEmail() throws Exception {
        String email = "james@atu.ie";
        String jwt = "valid-jwt";
        Task task = new Task("1", "Test Task", "In Progress", "Information", email);
        List<Task> tasks = Collections.singletonList(task);

        when(taskService.getAllTasksForUser(email, jwt)).thenReturn(ResponseEntity.ok(tasks));

        mockMvc.perform(get("/allTasks/{email}", email)
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService, times(1)).getAllTasksForUser(email, jwt);
    }

    @Test
    void createTaskFail() throws Exception {
        String jwt = "invalid-jwt";
        Task task = new Task(null, "", "", "", "james@atu.ie");
        String taskJson = objectMapper.writeValueAsString(task);

        when(taskService.createTask(task, jwt)).thenReturn(ResponseEntity.status(406).body("Incorrect Authorization Header"));

        mockMvc.perform(post("/createTask")
                        .header("Authorization", jwt)
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string("Incorrect Authorization Header"));

        verify(taskService, times(1)).createTask(task, jwt);
    }

    @Test
    void updateTaskSuccess() throws Exception {
        String email = "james@atu.ie";
        String jwt = "valid-jwt";
        String id = "1";
        Task updatedTask = new Task("1", "Updated Task", "Completed", "Updated Information", email);
        String taskJson = objectMapper.writeValueAsString(updatedTask);

        when(taskService.updateTask(id, email, updatedTask, jwt)).thenReturn(ResponseEntity.ok("Task updated: " + updatedTask.getTitle()));

        mockMvc.perform(put("/updateTask/{email}/{id}", email, id)
                        .header("Authorization", jwt)
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Task updated: Updated Task"));

        verify(taskService, times(1)).updateTask(id, email, updatedTask, jwt);
    }

    @Test
    void deleteTaskSuccess() throws Exception {
        String email = "james@atu.ie";
        String jwt = "valid-jwt";
        String id = "1";

        when(taskService.deleteTask(id, email, jwt)).thenReturn(ResponseEntity.ok("Task deleted successfully: Test Task"));

        mockMvc.perform(delete("/deleteTask/{email}/{id}", email, id)
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted successfully: Test Task"));

        verify(taskService, times(1)).deleteTask(id, email, jwt);
    }
}
