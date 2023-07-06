package org.jda.example.coursemanmsa.coursemgnt.modules.studentclass.repository;

import org.jda.example.coursemanmsa.coursemgnt.modules.studentclass.model.StudentClass;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentClassRepository extends PagingAndSortingRepository<StudentClass,Integer>  {
}
