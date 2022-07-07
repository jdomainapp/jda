package org.jda.example.coursemanmsa.enrolment.repository;

import org.jda.example.coursemanmsa.enrolment.model.Coursemodule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseModuleRepository extends CrudRepository<Coursemodule,Integer>  {
}
