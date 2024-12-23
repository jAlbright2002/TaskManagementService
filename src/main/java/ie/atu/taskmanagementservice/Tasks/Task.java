package ie.atu.taskmanagementservice.Tasks;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title cannot be greater than 100 characters.")
    private String title;

    @NotBlank(message = "Status is required.")
    @Size(max = 50, message = "Status cannot be greater than 50 characters.")
    private String status;

    @Size(max = 500, message = "Description cannot be greater than 500 characters.")
    private String description;

    @Email(message = "Invalid email format.")
    private String email;

}
