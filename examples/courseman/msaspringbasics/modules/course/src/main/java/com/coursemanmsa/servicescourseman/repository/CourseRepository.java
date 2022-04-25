package com.coursemanmsa.servicescourseman.repository;

import org.springframework.data.repository.CrudRepository;

import com.coursemanmsa.servicescourseman.model.Course;

public interface CourseRepository extends CrudRepository<Course, Integer> {
//public  List<Course> findByName(String q);
}
