package ua.polischuk.entity;

import lombok.*;
import ua.polischuk.entity.enumsAndRegex.Category;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "Tests", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Test implements Serializable, Comparable<Test> {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    @Column( nullable = false)
    private Long id;


    @Column( nullable = false)
    private String name;



    @Column( nullable = false)
    private String name_ru;


    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private Category category;


    @Column( nullable = false)
    private Integer difficulty; // from 1 to 10


    @Column( nullable = false)
    private Integer numberOfQuestions;

    @Column( nullable = false)
    private int timeLimit;   //minutes

   /* @ManyToMany(fetch = FetchType.EAGER, mappedBy = "availableTests")
    private List<User> users;*/

    @Column
    private boolean active;

    @Column
    private Integer result;


    @Override
    public int compareTo(Test t) {
        return name.compareTo(t.getName());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return name.equals(test.name) &&
                category == test.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category);
    }
}
