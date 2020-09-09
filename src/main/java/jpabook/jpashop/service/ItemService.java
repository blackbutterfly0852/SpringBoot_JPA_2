package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item.Book;
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

    // 1-1. 변경 감지 == merge가 작동하는 원리
     //itemService.updateItem(itemId, form.getStockQuantity(), form.getAuthor(), form.getIsbn());
    @Transactional
    public Item updateItem(Long itemId, int stockQuantity, String author, String isbn){
        // 영속 상테 엔티티 -> commit -> 플러쉬(변경내역감지) -> update
        Item findItem = itemRepository.findOne(itemId);
        //findItem.setStockQuantity(stockQuantity);
        //findItem.setName(param.getName());
        //findItem.setStockQuantity(param.getStockQuantity());

        // 설계 시 주의점, 의미 있는 메소드로 변경 감지 update 진행 -> 추적의 용이성
        findItem.changeItem(stockQuantity, author, isbn);
        return findItem;
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
