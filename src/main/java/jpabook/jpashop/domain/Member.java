
package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;
    @Embedded
    private Address address;
    @OneToMany(mappedBy = "member") // Member 입장에서
    private List<Order> orders = new ArrayList<Order>(); // 객체 생성후 Collection은 변경 X, 하이버네이트 관리 메커니즘 차원, 안정성


}