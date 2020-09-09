package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.*;


@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    // 1. 상품 저장
    public void save(Item item) {
        if (item.getId() == null) { // JPA에 저장하기 전까지 ID 값이 없다. -> 새로 생성 객체(INSERT)
            em.persist(item);
        } else { // 이미 저장된 것을 가져온다.(UPDATE)
            Item merge = em.merge(item); // item은 파라미터로 넘어온 준영속상태, 리턴값은 영속상태
        }
    }

    // 2. 상품 단건 조회
    public Item findOne(Long id){
        return em.find(Item.class,id);
    }

    // 3. 상품 전체 조회
    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

}
