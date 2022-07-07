package org.jda.example.coursemanmsa.course.repository;

import org.jda.example.coursemanmsa.course.model.domain.Compulsorymodule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompulsoryModuleRepository extends CrudRepository<Compulsorymodule,Integer>  {
}
