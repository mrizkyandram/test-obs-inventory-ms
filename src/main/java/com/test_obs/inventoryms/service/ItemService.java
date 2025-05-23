package com.test_obs.inventoryms.service;

import com.test_obs.inventoryms.dto.ItemDTO;
import com.test_obs.inventoryms.exception.ResourceNotFoundException;
import com.test_obs.inventoryms.model.Item;
import com.test_obs.inventoryms.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        Integer remainingStock = itemRepository.findRemainingStockByItemId(id);
        item.setRemainingStock(remainingStock);

        return convertToDTO(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO> getAllItems(Pageable pageable) {
        Page<Item> items = itemRepository.findAll(pageable);
        return items.map(item -> {
            Integer remainingStock = itemRepository.findRemainingStockByItemId(item.getId());
            item.setRemainingStock(remainingStock);
            return convertToDTO(item);
        });
    }

    @Transactional
    public ItemDTO saveItem(ItemDTO itemDTO) {
        Item item = convertToEntity(itemDTO);
        Item savedItem = itemRepository.save(item);
        // New items have no stock yet
        savedItem.setRemainingStock(0);
        return convertToDTO(savedItem);
    }

    @Transactional
    public ItemDTO updateItem(Long id, ItemDTO itemDTO) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }

        Item item = convertToEntity(itemDTO);
        item.setId(id);
        Item updatedItem = itemRepository.save(item);

        Integer remainingStock = itemRepository.findRemainingStockByItemId(id);
        updatedItem.setRemainingStock(remainingStock);

        return convertToDTO(updatedItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setRemainingStock(item.getRemainingStock());
        return dto;
    }

    private Item convertToEntity(ItemDTO dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        return item;
    }
}