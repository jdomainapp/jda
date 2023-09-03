package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.studentclass.repository;

import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.studentclass.model.StudentClass;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentClassRepository extends PagingAndSortingRepository<StudentClass,Integer>  {
}
