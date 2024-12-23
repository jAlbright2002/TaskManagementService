package ie.atu.taskmanagementservice.Tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    private String id;

    private String title;

    private String status;

    private String description;

    private String email;

}
