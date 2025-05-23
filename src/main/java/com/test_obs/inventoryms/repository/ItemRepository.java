package com.test_obs.inventoryms.repository;

import com.test_obs.inventoryms.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT COALESCE(SUM(CASE WHEN i.type = 'T' THEN i.quantity ELSE -i.quantity END), 0) " +
            "FROM Inventory i WHERE i.item.id = :itemId")
    Integer findRemainingStockByItemId(@Param("itemId") Long itemId);
}
