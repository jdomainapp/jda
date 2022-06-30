package org.jda.example.coursemanmsa.assessmenthub.modules.teacher.repository;

import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher,Integer>  {
}
