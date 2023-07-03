package org.jda.example.coursemanmsa.assessmenthub.modules.address.repository;

import org.jda.example.coursemanmsa.assessmenthub.modules.address.model.Address;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address,String>  {
}
