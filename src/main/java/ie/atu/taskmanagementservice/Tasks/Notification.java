package ie.atu.taskmanagementservice.Tasks;

import lombok.Data;

@Data
public class Notification {

    private String id;

    private String actionType;

    private String message;

    private String dateOfAction;

    private String email;

    private boolean isRead;
}
