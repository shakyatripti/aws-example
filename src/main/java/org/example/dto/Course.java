package org.example.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private int id;
    private String name;
    private double price;
    private String description;
}
