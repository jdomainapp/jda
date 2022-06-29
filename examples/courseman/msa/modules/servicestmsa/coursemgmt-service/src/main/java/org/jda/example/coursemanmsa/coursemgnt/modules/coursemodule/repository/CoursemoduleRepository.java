package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.repository;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseModuleRepository extends CrudRepository<Coursemodule,Integer>  {
}
