package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 특정 화면에 대한 FIX
// 화면 처리에 대한 라이프사이클 != 핵심 비즈니스 로직에 대한 라이프 사이클
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;
    // OrderApiController.java 의 OrderDto class 참조 X :
    // repository가 controller를 참조해 버리는 순환관계.
    public List<OrderQueryDto> findOrders(){
        // new 연산자 사용? SQL 처럼 사용하기 때문에 컬렉션을 사용할 수 없다.
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name,o.orderDate,o.status,d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    // 컬렉션 처리하기 위한 로직
    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders();
        // 루프를 돌면서 orderItem 가져온다.
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }


    private List<OrderItemQueryDto> findOrderItems(Long orderId){
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id =:orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();

    }



    // V5 DTO 조회 최적화 -> LOOP 안돌고 한번에 가져온다.
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        // OrderId 두 개가 List로 변환 -> Map으로 변환
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap( toOrderIds(result));
        // 위의 Map을 변환한 걸 메모리상에서 대입
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems =  em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // K : orderId, V : OrderItemQueryDto
        // 바로 위의 값을 메모리상에서 매칭
        return orderItems.stream().collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                        .map(o -> o.getOrderId())
                        .collect(Collectors.toList());
    }

    // V6 한 번의 쿼리로 조회
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi " +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();

    }
}
