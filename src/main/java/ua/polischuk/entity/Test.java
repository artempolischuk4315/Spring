package ua.polischuk.entity;

import lombok.*;
import org.hibernate.validator.constraints.Range;
import ua.polischuk.entity.enumsAndRegex.Category;
import ua.polischuk.entity.enumsAndRegex.RegexContainer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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


    private static final int MIN = 1;
    private static final int MAX_DIF = 10;
    private static final int MAX_QUESTIONS = 100;
    private static final int MAX_TIME_LIMIT = 1000;
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    @Column( nullable = false)
    private Long id;


    @Pattern(regexp = RegexContainer.NAME_OF_TEST)
    @Column( nullable = false)
    private String name;



    @Pattern(regexp = RegexContainer.NAME_OF_TEST_RU)
    @Column( nullable = false)
    private String name_ru;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private Category category;


    @Range(min = MIN , max = MAX_DIF)
    @Column( nullable = false)
    private Integer difficulty; // from 1 to 10


    @Range(min = MIN , max = MAX_QUESTIONS)
    @Column( nullable = false)
    private Integer numberOfQuestions;

    @Range(min = MIN , max = MAX_TIME_LIMIT)
    @Column( nullable = false)
    private int timeLimit;   //minutes

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
