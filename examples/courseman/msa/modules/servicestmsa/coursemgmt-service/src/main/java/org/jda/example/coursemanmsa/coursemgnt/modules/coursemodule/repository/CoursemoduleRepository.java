package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.repository;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursemoduleRepository extends CrudRepository<Coursemodule,Integer>  {
}
