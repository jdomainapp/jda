package org.jda.example.coursemanmsa.assessmenthub.modules.student.repository;

import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student,String>  {
}
