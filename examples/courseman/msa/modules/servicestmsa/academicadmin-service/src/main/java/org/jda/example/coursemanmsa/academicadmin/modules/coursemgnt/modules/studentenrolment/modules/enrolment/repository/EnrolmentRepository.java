package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.enrolment.repository;

import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.enrolment.model.Enrolment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrolmentRepository extends PagingAndSortingRepository<Enrolment,Integer>  {
	List<Enrolment> findByCoursemoduleId(int id);
}
