package org.jda.example.coursemanmsa.student.repository;

import org.jda.example.coursemanmsa.student.model.StudentClass;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentClassRepository extends CrudRepository<StudentClass,Integer>  {
}
