package org.jda.example.coursemanmsa.course.repository;

import org.jda.example.coursemanmsa.course.model.domain.Electivemodule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectiveModuleRepository extends CrudRepository<Electivemodule,Integer>  {
}
