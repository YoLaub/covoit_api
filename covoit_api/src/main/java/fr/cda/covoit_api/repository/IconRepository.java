package fr.cda.covoit_api.repository;

import fr.cda.covoit_api.domain.entity.Icon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IconRepository extends JpaRepository<Icon, Integer> {}