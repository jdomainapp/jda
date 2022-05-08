package org.jda.example.coursemanmsa.address.repository;

import org.jda.example.coursemanmsa.address.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address,Integer>  {
}
