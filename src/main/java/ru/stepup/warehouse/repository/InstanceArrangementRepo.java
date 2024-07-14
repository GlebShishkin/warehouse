package ru.stepup.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.stepup.warehouse.entity.InstanceArrangement;

public interface InstanceArrangementRepo  extends JpaRepository<InstanceArrangement, Integer> {
}
