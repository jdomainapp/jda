package org.jda.example.coursemanmsa.student.repository;

import org.jda.example.coursemanmsa.student.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address,Integer>  {
}
