package com.sazimtandabuzo.smallbusinessinvoices.repository;

import com.sazimtandabuzo.smallbusinessinvoices.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
