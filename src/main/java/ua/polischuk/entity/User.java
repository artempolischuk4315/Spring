package ua.polischuk.entity;

import lombok.*;
import ua.polischuk.entity.enumsAndRegex.RegexContainer;
import ua.polischuk.entity.enumsAndRegex.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.*;




@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString


@Entity
@Table( name="user",
        uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
public class User  {

    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Pattern(regexp = RegexContainer.NAME_REGEX)
    @Column( nullable = false)
    private String firstName;

    @Pattern(regexp = RegexContainer.NAME_REGEX_RU)
    @Column( nullable = false)
    private String firstName_ru;


    @Pattern(regexp = RegexContainer.NAME_REGEX)
    @Column( nullable = false)
    private String lastName;

    @Pattern(regexp = RegexContainer.NAME_REGEX_RU)
    @Column( nullable = false)
    private String lastName_ru;


    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roles;

    private double success;

    @ManyToMany( cascade = CascadeType.REMOVE)
    @JoinTable(name="user_availabletest",
            joinColumns = @JoinColumn(name="id", referencedColumnName="id")
    )
    private Set<Test> availableTests;


    @ElementCollection
    @CollectionTable(name="completed_tests")
    @MapKeyColumn(name="test_id")
    private Map<Test, Integer> resultsOfTests;

    private String activationCode;

    private boolean active;

    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = role;
    }

}