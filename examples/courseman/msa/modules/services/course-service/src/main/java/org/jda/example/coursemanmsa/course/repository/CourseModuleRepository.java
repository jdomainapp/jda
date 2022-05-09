package org.jda.example.coursemanmsa.course.repository;

import org.jda.example.coursemanmsa.course.model.domain.CourseModule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseModuleRepository extends PagingAndSortingRepository<CourseModule,Integer>  {
}
