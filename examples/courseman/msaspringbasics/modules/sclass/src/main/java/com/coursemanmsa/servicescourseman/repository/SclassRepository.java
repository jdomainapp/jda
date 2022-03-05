package com.coursemanmsa.servicescourseman.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.coursemanmsa.servicescourseman.model.Sclass;

@Repository
public interface SclassRepository extends CrudRepository<Sclass,Integer> {
}
