package org.jda.example.coursemanmsa.student.repository;

import java.util.List;

import org.jda.example.coursemanmsa.student.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student,String>  {
	public List<Student> findByAddressId (int addressId);
	public List<Student> findByStudentclassId (int studentclassId);
}
