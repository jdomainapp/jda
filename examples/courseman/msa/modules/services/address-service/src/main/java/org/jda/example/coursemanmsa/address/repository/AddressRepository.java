package org.jda.example.coursemanmsa.address.repository;

import java.util.Optional;

import org.jda.example.coursemanmsa.address.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address,String>  {
	public Optional<Address> findById(int addressId);
}
