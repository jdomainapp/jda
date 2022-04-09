package org.jda.example.coursemanmsa.student.repository;

import java.util.Optional;

import org.jda.example.coursemanmsa.student.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student,String>  {
	public Optional<Student> findById(String studentId);
}
