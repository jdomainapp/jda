package org.jda.example.coursemanmsa.course.repository;

import org.jda.example.coursemanmsa.course.model.domain.Compulsorymodule;
import org.jda.example.coursemanmsa.course.model.domain.CourseModule;
import org.jda.example.coursemanmsa.course.model.domain.Electivemodule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseModuleRepository extends PagingAndSortingRepository<CourseModule,Integer>  {
	Page<CourseModule> findByCompulsorymodule(Compulsorymodule arg0, Pageable pageable);
	Page<CourseModule> findByElectivemodule(Electivemodule arg0, Pageable pageable);
}
