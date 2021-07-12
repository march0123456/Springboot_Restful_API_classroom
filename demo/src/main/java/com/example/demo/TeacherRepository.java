package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TeacherRepository
        extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findTeacherByNameContaining(String name);
}
