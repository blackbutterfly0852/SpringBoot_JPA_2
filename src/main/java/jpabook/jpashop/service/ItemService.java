package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    // 1. 상품 저장
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    // 2. 상품 단건 조회
    public Item findOne(Long id){
       return itemRepository.findOne(id);
    }
    // 3. 상품 전체 조회

    public List<Item> findItems(){
        return itemRepository.findAll();
    }




}
