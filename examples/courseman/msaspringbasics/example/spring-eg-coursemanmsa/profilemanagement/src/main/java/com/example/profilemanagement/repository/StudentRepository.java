package com.example.profilemanagement.repository;

import org.springframework.stereotype.Repository;

import com.example.profilemanagement.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StudentRepository extends JpaRepository < Student, Integer >{

}
