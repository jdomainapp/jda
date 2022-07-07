package org.jda.example.coursemanmsa.academic.repository;

import org.jda.example.coursemanmsa.academic.model.Coursemodule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseModuleRepository extends CrudRepository<Coursemodule,Integer>  {
}
