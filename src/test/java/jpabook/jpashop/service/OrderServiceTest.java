package jpabook.jpashop.service;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
// 좋은 테스트는 DB 등 여러 개를 테스트하는 것 보다 순수하게 메소드만 테스트
public class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 주문생성() throws Exception {
        // given
        Member member = createMember();
        em.persist(member);
        Item book = createItem();
        em.persist(book);

        // when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        // then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품주문시 상태는 ORDER", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 상품 수 * 가격", 20000, getOrder.getTotalPrice());
        assertEquals("주문 후 남은 재고는 ", 8, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        // given
        Member member = createMember();
        em.persist(member);
        Item book = createItem();
        em.persist(book);
        // when
        int orderCount = 10;
        orderService.order(member.getId(), book.getId(), orderCount); // removeStock() 단위 테스트 중요
        // then
        fail("여기로 오면 안됩니다.");
    }

    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = createMember();
        em.persist(member);
        Item book = createItem();
        em.persist(book);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);
        Order getOrder = orderRepository.findOne(orderId);

        // then
        assertEquals("주문 상태는 CANCEL이 되어야 한다.", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, book.getStockQuantity());
    }


    public Member createMember() {
        Member member = new Member();
        member.setName("김동우");
        member.setAddress(new Address("서울", "강가", "123-123"));
        return member;
    }

    public Item createItem() {
        Item book = new Book();
        book.setName("자서전");
        book.setPrice(10000);
        book.setStockQuantity(10);
        return book;
    }
}