package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    // 1. 주문조회 V1 : 엔티티 직접 노출
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        // ordersV1 함수를 사용하기 위해서(엔티티직접노출) 양방향 연관관계는 @JsonIgnore 필요, 여기서는 Member.java 와 OrderItem.java에 필요
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // Lazy 로딩을 해서 정상적으로 프록시 초기화된 필드만 API로 반환된다.
        for (Order order : all) {
            //order.getMember() // 여기까지의 member는 프록시 객체
            order.getMember().getName(); // Lazy 강제 초기화
            order.getMember().getAddress(); // Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // 각각의 item도 Lazy 강제 초기화

        }
        return all;
    }

    // 2. 주문조회 V2 : DTO 변환 -> 쿼리가 多(1+N)
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return collect;
    }

    // 3. 주문조회 V3
    // 컬렉션 패치 조인
    // 문제 : Order 데이터가 OrderItem 만큼 데이터가 많아진다(뻥튀기)
    // 해결책 : OrderRepository.java findAllWithItem()의 distinct 추가 (DB의 distinct 기능 + JPA가 같은 참조값이면, 즉 엔티티가 중복인 경우에 중복제거 후 컬렉션에 담는다)
    // 그러나 최대 단점 : 1:多  관계에서 패치 조인을 할 경우, 페이징 불가능
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders =  orderRepository.findAllWithItem();
        for (Order order : orders) {
            System.out.println("order ref=" + order + " id=" + order.getId()); // JPA는 PK가 값으면 참조 메모리도 동일하다
        }
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return collect;
    }


    // 3. 주문조회 V3.1
    // 컬렉션 + 페이징
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value ="offset", defaultValue = "0") int offset,
                                        @RequestParam(value ="limit", defaultValue = "100") int limit) {
        // 1) ToOne 관계는 모두 패치 조인으로 한 방 쿼리로 변경
        List<Order> orders =  orderRepository.findAllWithMemberDelivery(offset,limit);
        // 2) ToMany 관계는 orders가 LOOP 하면서 orderItem을 가져온다.
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return collect;
    }


    // 4. 주문조회 V4: JPA에서 DTO로 직접조회 -> 1+문제
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(  ) {
        return orderQueryRepository.findOrderQueryDtos();
    }

    // 5. 주문조회 V5: JPA에서 DTO로 직접조회 -> 1+N 문제 최적화 -> 쿼리 두번
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(  ) {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // 6. 주문조회 V6 : 한 번의 쿼리
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(  ) {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        // Order 중복데이터 분리.
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // 1), 2) private List<OrderItem> orderItems;
        // 3)
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            // 다시 프록시 초기화, 그러나 결국 엔티티를 반환, 엔티티에 대한 의존을 완전히 끊어야 한다.
            // OrderItem 자체도 DTO로 만들어야 한다.
            // 1) orderItems = order.getOrderItems();
            // 2) order.getOrderItems().stream().forEach(o -> o.getItem().getName());
            this.orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());;

        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem){
            this.itemName = orderItem.getItem().getName() ;
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }



    }
}
