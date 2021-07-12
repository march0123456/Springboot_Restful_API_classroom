package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CourseDTO {
    private Long cid;
    private String subject;
    private String schedule_Time;
    private Long maximum_Student;
    private Long tid;
    private Set<String> students = new HashSet<>();
}
