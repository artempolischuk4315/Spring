package ua.polischuk.dto;



import lombok.*;
import ua.polischuk.entity.User;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UsersDTO {
private List<User> users;
}
