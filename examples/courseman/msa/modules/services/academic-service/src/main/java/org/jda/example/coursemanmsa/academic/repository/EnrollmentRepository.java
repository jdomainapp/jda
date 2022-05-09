package org.jda.example.coursemanmsa.academic.repository;

import org.jda.example.coursemanmsa.academic.model.Enrolment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends PagingAndSortingRepository<Enrolment,Integer>  {
}
