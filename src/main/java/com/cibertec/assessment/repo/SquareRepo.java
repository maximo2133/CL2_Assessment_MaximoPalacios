package com.cibertec.assessment.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cibertec.assessment.model.Square;

public interface SquareRepo extends JpaRepository<Square, Integer> {

	@Query("SELECT s FROM Square s WHERE s.name = :name")
    List<Square> findByName(String name);
}
