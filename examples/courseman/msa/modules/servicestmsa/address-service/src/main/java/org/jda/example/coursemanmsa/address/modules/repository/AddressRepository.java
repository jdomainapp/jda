package org.jda.example.coursemanmsa.address.modules.repository;

import org.jda.example.coursemanmsa.address.modules.model.Address;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address,Integer>  {
}
