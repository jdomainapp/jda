package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.repository;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.CourseModule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursemoduleRepository extends PagingAndSortingRepository<CourseModule,Integer>  {
}
