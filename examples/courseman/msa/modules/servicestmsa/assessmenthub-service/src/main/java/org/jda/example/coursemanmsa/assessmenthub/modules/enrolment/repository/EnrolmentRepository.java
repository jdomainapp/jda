package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.repository;

import java.util.List;

import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrolmentRepository extends PagingAndSortingRepository<Enrolment,Integer>  {
	List<Enrolment> findByCoursemoduleId(int id);
}
