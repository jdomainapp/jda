package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.student.repository;

import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.student.model.Student;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student,String>  {
}
