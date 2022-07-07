package org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.repository;

import java.util.List;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrolmentRepository extends PagingAndSortingRepository<Enrolment,Integer>  {
	List<Enrolment> findByCoursemoduleId(int id);
}
