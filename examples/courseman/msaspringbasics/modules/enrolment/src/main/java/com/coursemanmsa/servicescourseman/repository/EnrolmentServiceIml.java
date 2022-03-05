package com.coursemanmsa.servicescourseman.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coursemanmsa.servicescourseman.model.Erolment;
import com.coursemanmsa.servicescourseman.service.EnrolmentService;

import java.util.List;
import java.util.Optional;

@Service
public class EnrolmentServiceIml implements EnrolmentService {
    @Autowired
    EnrolmentRepository repository;
    @Override
    public Erolment save(Erolment entity) {
        return repository.save(entity);
    }

    @Override
    public List<Erolment> saveAll(List<Erolment> entities) {

        return (List<Erolment>) repository.saveAll(entities);
    }

    @Override
    public Optional<Erolment> findById(Integer integer) {
        return repository.findById(integer);
    }

    @Override
    public boolean existsById(Integer integer) {
        return repository.existsById(integer);
    }

    @Override
    public List<Erolment> findAll() {
        return (List<Erolment>) repository.findAll();
    }

    @Override
    public List<Erolment> findAllById(List<Integer> integers) {
        return (List<Erolment>) repository.findAllById(integers);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(Integer integer) {
        repository.deleteById(integer);
    }

    @Override
    public void delete(Erolment entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAllById(List<Integer> integers) {
      // ducmle: fix error -> repository.deleteAllById(integers);
      for (Integer id: integers) {
        repository.deleteById(id);
      }      
    }

    @Override
    public void deleteAll(List<Erolment> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<Erolment> findErolmentByIdstudent(String q) {
        return repository.findErolmentByIdstudent(q);
    }

    @Override
    public List<Erolment> findErolmentByIdstudentContaining(String q) {
        return repository.findErolmentByIdstudentContaining(q);
    }

    @Override
    public List<Erolment> findByIdstudentLike(String q) {
        return repository.findByIdstudentLike(q);
    }

    @Override
    public List<Erolment> findErolmentsByIdstudentIs(Integer q) {
        return repository.findErolmentsByIdstudentIs(q);
    }

    @Override
    public List<Erolment> findErolmentsByIdstudentEquals(Integer q) {
        return repository.findErolmentsByIdstudentEquals(q);
    }
}
