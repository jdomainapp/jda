package org.jda.example.coursemanmsa.academic.repository;

import java.util.List;

import org.jda.example.coursemanmsa.academic.model.Academic;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicRepository extends PagingAndSortingRepository<Academic,Integer>  {
	List<Academic> findByCoursemoduleId(int id);
}
