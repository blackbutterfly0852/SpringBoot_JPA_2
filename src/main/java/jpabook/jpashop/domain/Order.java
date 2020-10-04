package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
// 생성 메소드로만 orderItem을 생성할 수 있도록 제한 2
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // order의 입장
    @JoinColumn(name = "member_id") // 연관관계 주인으로서, 맵핑을 하겠다.
    private Member member;

    //@BatchSize(size = 1000)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // Order를 persist() 하면 OrderItem도 강제로 persist() 해준다.
    private List<OrderItem> orderItems = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Order를 persist() 하면 delivery도 강제로 persist() 해준다.
    @JoinColumn(name = "delivery_id") // 연관관계 주인으로서, 맵핑을 하겠다.
    private Delivery delivery;

    private LocalDateTime orderDate; // Java 8, 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCLE]


    // 연관관계 메서드 -> 연관관계 있는 Entity 중 Contoller 하는 부분에 작성
    // Order의 입장에서
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        this.orderItems.add(orderItem);
    }

    // 생성 메서드 //
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {

        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 비즈니스 로직
    // 1. 주문 취소
    public void cancel() {

        if (this.delivery.getStatus().equals(DeliveryStatus.COMP)) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능 합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel();
        }

    }

    // 2. 전체 주문 가격 로직
     public int getTotalPrice(){
        int totalPrice = 0;
        // Alt + Enter -> Replace with sum -> 코드 깔끔하게 정리
        for (OrderItem orderItem : this.orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }


}
