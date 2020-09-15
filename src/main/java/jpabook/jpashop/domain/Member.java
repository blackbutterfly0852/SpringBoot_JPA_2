
package jpabook.jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //@NotEmpty // @Valid 값 체크 -> Entity에 프레젠테이션 계층을 녹이는 것은 유지보수성에 않좋음
    private String name;
    @Embedded
    private Address address;

    //@JsonIgnore
    // 회원조회 API 시 주문내역을 제외하기 위한 API -> Entity에 프레젠테이션 계층을 녹이는 것은 유지보수성에 않좋음
    // 양뱡향 연관관계 시 한 쪽은 무시해야 함 ex) Order.java 기준 Member.java의 orders @JsonIgnore
    @OneToMany(mappedBy = "member") // Member 입장에서
    private List<Order> orders = new ArrayList<Order>(); // 객체 생성후 Collection은 변경 X, 하이버네이트 관리 메커니즘 차원, 안정성


}