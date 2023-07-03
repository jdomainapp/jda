package org.jda.example.coursemanmsa.studentclass.modules.repository;

import org.jda.example.coursemanmsa.studentclass.modules.model.StudentClass;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentClassRepository extends PagingAndSortingRepository<StudentClass,Integer>  {
}
