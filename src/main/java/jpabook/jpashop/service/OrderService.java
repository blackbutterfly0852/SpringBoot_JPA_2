package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 1. 주문생성
    // 사용자 입장에서 생각 : 주문_ID, 상품_ID(한 개만 구매하도록), 수량을 선택에서 주문한다.
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 1) 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 2) 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // 3) 주문상품 생성
        // 생성 방법을 여러개 사용하는 경우, 유지보수성 떨어짐,
        // 하나의 방법으로만 생성할 수 있도록 protected 생성자 작성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 4) 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 5) 주문 저장
        orderRepository.save(order); // Order Entity에서 casecade 제약 조건으로 인해, delivery, orderItem을 강제로 persist()를 해준다.
                                     // cascade의 사용범위?  1. Delivery, OrderItem의 오너가 Order밖에 없을 경우 (Private Owner) 2. Persist할 라이프 사이클이 동일한 경우
                                     // 그렇지 않는데 cascade를 사용하는 경우, 삭제되지 말아야할 것이 삭제 될 수 있다.
                                     // 처음에는 cascade를 안쓰다가, 2가지 조건에 익숙해지면 코드 리팩토링
        return order.getId();
    }

    // 2. 주문취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        // JPA 강점 -> 이전에 UPDATE 구문을 작성 했으나, 안해도됨 -> Dirty Check, 변경내역 감지, 업데이트 쿼리 자동 생성
        order.cancel();
    }

    // 3. 주문조회
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    }



}
