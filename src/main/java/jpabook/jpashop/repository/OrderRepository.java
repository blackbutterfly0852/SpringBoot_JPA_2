package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    // 1. 주문 저장
    public void save(Order order){
        em.persist(order);
    }

    // 2. 주문 단건 조회
    public Order findOne(Long id){
        return em.find(Order.class,id);
    }

    // 3. 동적 쿼리
}
