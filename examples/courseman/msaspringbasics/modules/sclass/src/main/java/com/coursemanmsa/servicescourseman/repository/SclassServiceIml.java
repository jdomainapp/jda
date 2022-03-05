package com.coursemanmsa.servicescourseman.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.coursemanmsa.servicescourseman.model.Sclass;
import com.coursemanmsa.servicescourseman.service.SclassService;


@Service
public class SclassServiceIml implements SclassService {
    @Autowired
    SclassRepository repository;
    @Override
    public Sclass save(Sclass entity) {
        return repository.save(entity);
    }

    @Override
    public List<Sclass> saveAll(List<Sclass> entities) {

        return (List<Sclass>) repository.saveAll(entities);
    }

    @Override
    public Optional<Sclass> findById(Integer integer) {
        return repository.findById(integer);
    }

    @Override
    public boolean existsById(Integer integer) {
        return repository.existsById(integer);
    }

    @Override
    public List<Sclass> findAll() {
        return (List<Sclass>) repository.findAll();
    }

    @Override
    public List<Sclass> findAllById(List<Integer> integers) {
        return (List<Sclass>) repository.findAllById(integers);
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
    public void delete(Sclass entity) {
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
    public void deleteAll(List<Sclass> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
