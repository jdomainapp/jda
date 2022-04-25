package com.coursemanmsa.servicescourseman.service;



import java.util.List;
import java.util.Optional;

import com.coursemanmsa.servicescourseman.model.Student;

public interface StudentService {
    Student save(Student entity);

    List<Student> saveAll(List<Student> entities);

    Optional<Student> findById(Integer integer);

    boolean existsById(Integer integer);

    List<Student> findAll();

    List<Student> findAllById(List<Integer> integers);

    long count();

    void deleteById(Integer integer);

    void delete(Student entity);

    void deleteAllById(List<Integer> integers);

    void deleteAll(List<Student> entities);

    void deleteAll();
}
