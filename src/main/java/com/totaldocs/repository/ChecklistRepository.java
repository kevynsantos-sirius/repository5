package com.totaldocs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.Checklist;



public interface ChecklistRepository extends JpaRepository<Checklist, Integer> {
}
