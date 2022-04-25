package com.coursemanmsa.servicescourseman.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.coursemanmsa.servicescourseman.model.Erolment;

import java.util.List;

@Repository
public interface EnrolmentRepository extends CrudRepository<Erolment,Integer> {
    List<Erolment> findErolmentByIdstudent(String q);
    List<Erolment> findErolmentByIdstudentContaining(String q);
    List<Erolment> findByIdstudentLike(String q);
    List<Erolment> findErolmentsByIdstudentIs(Integer q);
    List<Erolment> findErolmentsByIdstudentEquals(Integer q);
}
