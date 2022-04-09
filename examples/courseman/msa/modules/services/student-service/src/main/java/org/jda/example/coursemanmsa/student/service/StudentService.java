package org.jda.example.coursemanmsa.student.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.student.config.ServiceConfig;
import org.jda.example.coursemanmsa.student.model.Student;
import org.jda.example.coursemanmsa.student.model.Address;
import org.jda.example.coursemanmsa.student.repository.StudentRepository;
import org.jda.example.coursemanmsa.student.service.client.AddressRestTemplateClient;
import org.jda.example.coursemanmsa.student.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class StudentService {

	@Autowired
	MessageSource messages;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	ServiceConfig config;


	@Autowired
	AddressRestTemplateClient addressRestClient;

	private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

	@CircuitBreaker(name = "studentService", fallbackMethod = "buildFallbackStudent")
	@RateLimiter(name = "studentService", fallbackMethod = "buildFallbackStudent")
	@Retry(name = "retryStudentService", fallbackMethod = "buildFallbackStudent")
	@Bulkhead(name = "bulkheadStudentService", type= Type.THREADPOOL, fallbackMethod = "buildFallbackStudent")
	public Student getStudent(String studentId) throws TimeoutException{
		Optional<Student> opt = studentRepository.findById(studentId);
		Student student = (opt.isPresent()) ? opt.get() : null;
		if (null == student) {
			throw new IllegalArgumentException(String.format(messages.getMessage("student.search.error.message", null, null),""+ studentId));	
		}
		logger.debug("getStudent Correlation id: {}",
				UserContextHolder.getContext().getCorrelationId());
		//sleep();
		Address address = retrieveAddressInfo(student.getAddressId());
		if (null != address) {
			student.setAddressName(address.getName());
		}

		return student;
	}

	//@CircuitBreaker(name = "studentService", fallbackMethod = "buildFallbackAddress")
	public Address retrieveAddressInfo(int addressId) throws TimeoutException {
		Address address = addressRestClient.getAddress(""+addressId);

		return address;
	}

	@SuppressWarnings("unused")
	private Student buildFallbackStudent(String studentId, Throwable t){
		Student failStudent = new Student();
		failStudent.setStudentName("Resilience");
		return failStudent;
	}
	
//	@SuppressWarnings("unused")
//	private Address buildFallbackAddress(int addressId, Throwable t){
//		Address failAddress = new Address();
//		failAddress.setName("CHAN");
//		return failAddress;
//	}

	private void sleep() throws TimeoutException{
		try {
			System.out.println("Sleep");
			Thread.sleep(5000);
			throw new java.util.concurrent.TimeoutException();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
	
	public List<Student> getStudents(){
		List<Student> studentList = new ArrayList<>();
		Iterable<Student> students = studentRepository.findAll();
		students.forEach(s -> studentList.add(s));
		return studentList;
	}

}
