package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class) // Spring과 통합 테스트
@SpringBootTest // Spring과 통합 테스트
@Transactional // 롤백용

public class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    // @Rollback(false)
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("김동우");

        // when
        // @Transactional : persist() 호출하지만 DB에 INSERT X(Default), Rollback
        // @Rollback(false) : DB COMMIT시 플러쉬가 되면서 영속성 컨테스트 객체가 저장된다
        Long saveId = memberService.join(member);

        em.flush(); // @Transactional에서 Rollback은 되지만 insert 쿼리는 보고 싶은 경우
        // then
        assertEquals(member, memberRepository.findOne(saveId)); // @Transactional  같은 트랜잭션 안에서 PK값이 동일하면 영속성 컨테스트에서 동일한 걸로 간주.
    }

    @Test(expected = IllegalStateException.class) // 속성 추가시 아래 try catch 안써도됨.
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        memberService.join(member2);
        /*try{
            memberService.join(member2); // 예외가 발생해야 한다.
        }catch(IllegalStateException e){
            return;
        }*/

        // then
        fail("예외가 발생해야합니다."); // 여기 오면 안되기 위함.
    }

}