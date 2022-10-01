package org.jda.example.coursemanmsa.service.modules.teacher.repository;

import org.jda.example.coursemanmsa.service.modules.teacher.model.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends PagingAndSortingRepository<Teacher,Integer>  {
}
