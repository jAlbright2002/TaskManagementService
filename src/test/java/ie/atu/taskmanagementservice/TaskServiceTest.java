package ie.atu.taskmanagementservice;

import ie.atu.taskmanagementservice.Config.JwtStore;
import ie.atu.taskmanagementservice.Tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskDB taskDB;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private JwtStore jwtStore;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testGetAllTasksForUserSuccess() {
        String email = "test@atu.ie";
        String jwt = "valid-jwt";
        Task task = new Task("1", "Task 1", "To Do", "Information", email);
        when(taskDB.findAllByEmail(email)).thenReturn(Optional.of(List.of(task)));
        when(jwtStore.getJwt()).thenReturn("valid-jwt");

        ResponseEntity<List<Task>> response = taskService.getAllTasksForUser(email, jwt);

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Task 1", response.getBody().get(0).getTitle());
    }

    @Test
    void testGetAllTasksForUserNotFound() {
        String email = "test@atu.ie";
        String jwt = "valid-jwt";
        when(taskDB.findAllByEmail(email)).thenReturn(Optional.empty());
        when(jwtStore.getJwt()).thenReturn("valid-jwt");

        ResponseEntity<List<Task>> response = taskService.getAllTasksForUser(email, jwt);

        assertEquals(404, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testCreateTaskSuccess() {
        Task task = new Task("1", "New Task", "To Do", "Information", "test@atu.ie");
        String jwt = "valid-jwt";
        when(jwtStore.getJwt()).thenReturn("valid-jwt");

        ResponseEntity<String> response = taskService.createTask(task, jwt);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Task created: New Task"));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("createTaskSendNotificationQueue"), any(Notification.class));
    }

    @Test
    void testCreateTaskInvalidJwt() {
        Task task = new Task("1", "New Task", "To Do", "Information", "test@atu.ie");
        String jwt = "invalid-jwt";
        when(jwtStore.getJwt()).thenReturn("valid-jwt");

        ResponseEntity<String> response = taskService.createTask(task, jwt);

        assertEquals(406, response.getStatusCode().value());
        assertEquals("Incorrect Authorization Header", response.getBody());
    }

    @Test
    void testUpdateTaskSuccess() {
        String id = "1";
        String email = "test@atu.ie";
        Task oldTask = new Task(id, "Old Task", "To Do", "Information", email);
        Task updatedTask = new Task(id, "Updated Task", "In Progress", "More Information", email);
        String jwt = "valid-jwt";

        when(taskDB.findById(id)).thenReturn(Optional.of(oldTask));
        when(taskDB.existsById(id)).thenReturn(true);
        when(jwtStore.getJwt()).thenReturn("valid-jwt");

        ResponseEntity<String> response = taskService.updateTask(id, email, updatedTask, jwt);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Task updated: Updated Task"));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("updateTaskSendNotificationQueue"), any(Notification.class));
    }

    @Test
    void testDeleteTaskSuccess() {
        String id = "1";
        String email = "test@atu.ie";
        Task task = new Task(id, "Task to Delete", "In Progress", "Information", email);
        String jwt = "valid-jwt";

        when(taskDB.findById(id)).thenReturn(Optional.of(task));
        when(taskDB.existsById(id)).thenReturn(true);
        when(jwtStore.getJwt()).thenReturn("valid-jwt");

        ResponseEntity<String> response = taskService.deleteTask(id, email, jwt);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Task deleted successfully"));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("deleteTaskSendNotificationQueue"), any(Notification.class));
    }

    @Test
    void testDeleteTaskInvalidJwt() {
        String id = "1";
        String email = "test@atu.ie";
        String jwt = "invalid-jwt";
        when(jwtStore.getJwt()).thenReturn("valid-jwt");

        ResponseEntity<String> response = taskService.deleteTask(id, email, jwt);

        assertEquals(406, response.getStatusCode().value());
        assertEquals("Incorrect Authorization Header", response.getBody());
    }
}
