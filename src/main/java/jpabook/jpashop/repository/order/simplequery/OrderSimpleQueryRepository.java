package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    // OrderSimpleApiController.java V4 별도의 DTO 변환작업 없이 조회용
    // 참고 : 의존관계 흐름은 단반향으로(C->S->R), R->C? 망함
    // JPA는 엔티티나, 값타입만 변환 가능, DTO는 자동 변환 불가.
    // DTO 변환을 위해 별도 작업 필요 -> 원하는 컬럼만 선택 가능
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id,m.name,o.orderDate,o.status,d.address) from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class).getResultList();
    }

}
