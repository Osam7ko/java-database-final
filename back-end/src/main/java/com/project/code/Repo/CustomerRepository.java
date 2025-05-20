package com.project.code.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// Find a customer by their email
	Customer findByEmail(String email);

	@Override
	Optional<Customer> findById(Long id);
}
