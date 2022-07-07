package org.jda.example.coursemanmsa.studentclass.repository;

import org.jda.example.coursemanmsa.studentclass.model.StudentClass;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends PagingAndSortingRepository<StudentClass,Integer>  {
}
