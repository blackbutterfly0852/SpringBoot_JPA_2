package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // order의 입장
    @JoinColumn(name = "member_id") // 연관관계 주인으로서, 맵핑을 하겠다.
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // 연관관계 주인으로서, 맵핑을 하겠다.
    private Delivery  delivery;

    private LocalDateTime orderDate; // Java 8, 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCLE]


    // 연관관계 메서드 -> 연관관계 있는 Entity 중 Contoller 하는 부분에 작성
    // Order의 입장에서
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItem.setOrder(this);
        this.orderItems.add(orderItem);
    }






}
