package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;


    @Test
    public void 상품저장() throws Exception {
        // given
        Item book = createItem();
        itemService.saveItem(book);
        // when
        Item book2 = itemService.findOne(book.getId());
        // then
        assertEquals(book.getId(), book2.getId());

    }


    @Test
    public void 재고예외처리() throws Exception {
        // given
        Item book = createItem();
        itemService.saveItem(book);
        // when
        // 1) 재고 추가시
        //book.addStockQuantity(1000);
        // 2) 재고 제거시
        book.subStockQuantity(1000);
        // then
        // 1) 재고 추가시
        //assertEquals(3000, book.getStockQuantity());
        // 2) 재고 제거시
        fail("not here");

    }

    public Item createItem() {
        Item book = new Book();
        book.setName("김동우");
        book.setStockQuantity(1000);
        return book;
    }
}