package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Component 대상으로 자동으로 Spring Bean의 등록
@Transactional // 트랜잭션 안에서 데이터 변경 하기 위해 필요(JAVA_X 는 말고 SPRING 사용), 속성 적용은 여기가 우선
//@AllArgsConstructor // 주입방법 4 : 주입방법 3를 대신
@RequiredArgsConstructor // 주입방법 5 : final로 작성된 필드만 생성자 생성
public class MemberService {

    // 주입방법 1 : 변경이 불가능하다.
    // @Autowired
    private final MemberRepository memberRepository; // final은 주입방법 3으로 인해

    // 주입방법 2 : Setter Injection , TEST시 변경이 가능하다, 근데 운영시 변경 가능하기 때문에 문제 발생
    /*@Autowired
    public void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }*/

    // 주입방법 3: 생성자 Injection 주입방법 2의 문제를 보완
    /*@Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }*/



    // 1. 회원 가입
    @Transactional
    public Long join(Member member) {
        // 1-1. 중복회원 검증
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId(); // 항상 값이 보장된다. DB에 안들어가도 KEY를 생성하여 ID에 넣어주기 때문에
    }

    private void validateDuplicateMember(Member member) {
        if (!memberRepository.findByName(member.getName()).isEmpty()) { // 서버가 여러 대인 경우, 동시에 DB에 SAVE시 문제 발생 가능성 있음,
                                                                        // 이럴 경우 NAME을 unique 제약 조건 추가
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 2. 회원 전체 조회
    @Transactional(readOnly = true) // 조회시 최적화
    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    // 3. 회원 단건 조회
    @Transactional(readOnly = true) // 조회시 최적화
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
