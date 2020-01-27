package ua.polischuk.dto;

import lombok.*;
import ua.polischuk.entity.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString


public class TestsDTO {
    private List<Test> tests;

}
