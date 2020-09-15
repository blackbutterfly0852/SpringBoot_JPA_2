package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name ="delivery_id")
    private Long id;

    // @JsonIgnore
    // 양뱡향 연관관계 시 한 쪽은 무시해야 함 ex) Order.java 기준 Delivery.java의 order @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // ORDINAL(숫자)
    private DeliveryStatus status; // READY, COMP

}
