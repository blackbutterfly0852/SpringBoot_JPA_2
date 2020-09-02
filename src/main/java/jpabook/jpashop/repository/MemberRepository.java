package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository // Component 스캔의 의해 자동으로 Bean으로 관리
            // @SpringBootApplication 있는 곳과 동일한 패키지 내 있는 모든 객체는 Component Scan 대상
@RequiredArgsConstructor
public class MemberRepository {

    // @PersistenceContext // Spring이 EntityManger를 만들어서 주입, 주석 친 이유는 @RequiredArgsConstructor 때문이며, MemberService.java 참고
    // EntityManager는 @AutoWird로 주입이 안되고, @PersistenceContext 주입해야 되나, Spring Boot가 @AutoWird 지원, 그래서 @RequiredArgsConstructor 가능
    private final EntityManager em;

   /*  @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;*/

    // 1. member 저장
    public void save(Member member){
        em.persist((member)); // 영속성 컨테스트가 맴버객체를 올린다. DB에 안들어가도 KEY를 생성하여 ID에 넣어주기 때문에.
    }
    // 2. 단건 조회
    public Member findOne(Long id){
        Member member = em.find(Member.class, id);
        return member;
    }

    // 3. 모두 조회(JPQL, return Class)
    public List<Member> findAll(){
        // JPQL : 기능적으로 SQL과 거의 동일, JPQL은 Entity 객체를 통한 조회, SQL은 테이블 대상으로 조회
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    // 4. 이름으로 조회
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class ).setParameter("name", name).getResultList();
    }
}
