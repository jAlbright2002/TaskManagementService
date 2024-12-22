package ie.atu.taskmanagementservice.Tasks;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Task {

    @Id
    private String id;

    private String title;

    private String status;

    private String description;

    private String email;

}
