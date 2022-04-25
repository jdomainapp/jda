package com.coursemanmsa.servicescourseman.repository;


import org.springframework.data.repository.CrudRepository;

import com.coursemanmsa.servicescourseman.model.Student;

public interface StudentRepository extends CrudRepository<Student,Integer> {
}
