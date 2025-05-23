package com.test_obs.inventoryms.repository;

import com.test_obs.inventoryms.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT COUNT(o) FROM Order o")
    Long countOrders();
}
