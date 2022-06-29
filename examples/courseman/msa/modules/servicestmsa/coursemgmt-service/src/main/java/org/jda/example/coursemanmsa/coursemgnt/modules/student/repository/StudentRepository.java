package org.jda.example.coursemanmsa.coursemgnt.modules.student.repository;

import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student,String>  {
}
