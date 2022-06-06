package org.jda.example.coursemanmsa.enrolment.repository;

import org.jda.example.coursemanmsa.enrolment.model.Enrolment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends PagingAndSortingRepository<Enrolment,Integer>  {
}
