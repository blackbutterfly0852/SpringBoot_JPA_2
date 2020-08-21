package jpabook.jpashop;


import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository // DAO, Component 대상 -> 자동 bean 등록
public class MemberRepository {
    // Spring boot가 @PersistenceContext 어노테이션을 확인하면, em 자동주입
    @PersistenceContext
    private EntityManager em;

    // 커맨드와 쿼리는 분리해라
    // 저장 -> Side Effect의 요소 가능성 -> 최소 return값, 재조회 or 추후 추적
    public Long save(Member member){
        em.persist(member);
        return member.getId(); //
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }


}
