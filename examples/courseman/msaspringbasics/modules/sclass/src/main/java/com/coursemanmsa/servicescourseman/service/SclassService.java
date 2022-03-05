package com.coursemanmsa.servicescourseman.service;

import java.util.List;
import java.util.Optional;

import com.coursemanmsa.servicescourseman.model.Sclass;



public interface SclassService {
    Sclass save(Sclass entity);

    List<Sclass> saveAll(List<Sclass> entities);

    Optional<Sclass> findById(Integer integer);

    boolean existsById(Integer integer);

    List<Sclass> findAll();

    List<Sclass> findAllById(List<Integer> integers);

    long count();

    void deleteById(Integer integer);

    void delete(Sclass entity);

    void deleteAllById(List<Integer> integers);

    void deleteAll(List<Sclass> entities);

    void deleteAll();
}
