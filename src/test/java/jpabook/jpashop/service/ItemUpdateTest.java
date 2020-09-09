package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

        @Autowired
        EntityManager em;

        @Test
        public void SNAMES() throws Exception{
        // 영속성 엔티티 인 경우
        // 1) key 값이 1인 book을 가져와서
        Book book = em.find(Book.class, 1l);
        // 2) TX 내에서 변경 하면
        book.setName("asdfd");
        // 3) JPA가 변경감지(dirty checking)하여 TX 내에서 Commit -> update;

        // 준영속 엔티티 인 경우 : ItemController.java -> updateItem(), 이미 이전에 DB에 들어간 엔티티, 영속성 컨테스트가 더 이상 관려하지 않은 엔티티
        // 1) 준영속 엔티티는 JPA가 관리 X
        // 2) 개발자가 = new Book() 생성, 변경감지가 일어나지 않음.

        }
}
