package org.jda.example.coursemanmsa.enrolment.repository;

import org.jda.example.coursemanmsa.enrolment.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student,String>  {
}
