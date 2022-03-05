package com.coursemanmsa.servicescourseman.service;


import java.util.List;
import java.util.Optional;

import com.coursemanmsa.servicescourseman.model.Erolment;

public interface EnrolmentService {
    Erolment save(Erolment entity);

    List<Erolment> saveAll(List<Erolment> entities);

    Optional<Erolment> findById(Integer integer);

    boolean existsById(Integer integer);

    List<Erolment> findAll();

    List<Erolment> findAllById(List<Integer> integers);

    long count();

    void deleteById(Integer integer);

    void delete(Erolment entity);

    void deleteAllById(List<Integer> integers);

    void deleteAll(List<Erolment> entities);

    void deleteAll();

    List<Erolment> findErolmentByIdstudent(String q);

    List<Erolment> findErolmentByIdstudentContaining(String q);

    List<Erolment> findByIdstudentLike(String q);

    List<Erolment> findErolmentsByIdstudentIs(Integer q);

    List<Erolment> findErolmentsByIdstudentEquals(Integer q);
}
