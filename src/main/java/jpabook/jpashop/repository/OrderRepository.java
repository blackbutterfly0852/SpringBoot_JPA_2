package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    // 1. 주문 저장
    public void save(Order order) {
        em.persist(order);
    }

    // 2. 주문 단건 조회
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 3. 동적 쿼리
    public List<Order> findAllByString(OrderSearch orderSearch) {
        // 1) 회원 이름과, 주문 상태(ORDER, CANCEL)가 모두 있는 경우 -> 둘 중에 하나만 있는 경우, 둘 다 없는 경우 고려 X -> 동적 쿼리 필요
//        return  em.createQuery("select o from Order o join Member m" +
//                " where o.status = :status" +
//                " and m.name like :name", Order.class)
//                .setParameter("status" , orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) // 페이징 기법(최대 1000건)
//                .getResultList();

        // 2) 동적 쿼리 방법 1 -> 무식한 방법, 실무 X, 버그 생성 가능성 높음
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true; // WHERE 조건의 첫 번째, 두 번째 구분하기 위함.

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) { // getMemberName()의 값이 있으면
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        // 여기까지 기본적인 JPQL 작성이 되었다. 그런데 아직 파라미터 값은 전달 X

        // 파라미터 값 넣어주기
        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus()); // getOrderStatus()가 값이 있다면 파라미터 제공
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName()); // getMemberName()가 값이 있다면 파라미터 제공
        }
        return query.getResultList();
//    }
        // 3) 동적 쿼리 방법 2 -> JPA Criteria -> 실무 X
        // JPA 제공하는 동적 쿼리를 빌드해주는 즉, JPQL를 JAVA 코드로 작성할 수 있게끔, 제공하는 기능
        // 단점 : 쿼리가 그려지지 않는다. 유지보수성 낮아짐
//    public List<Order> findAllByCriteria(OrderSearch orderSearch){
//        // JPQL 생성
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
//        Root<Order> o = cq.from(Order.class); // Root 시작
//        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
//        List<Predicate> criteria = new ArrayList<>();
//
//        // 주문 상태 검색
//        if (orderSearch.getOrderStatus() != null) {
//            Predicate status = cb.equal(o.get("status"),orderSearch.getOrderStatus());
//            criteria.add(status);
//        }
//
//        //회원 이름 검색
//        if (StringUtils.hasText(orderSearch.getMemberName())) {
//            Predicate name = cb.like(m.<String>get("name"), "%" +
//                    orderSearch.getMemberName() + "%");
//            criteria.add(name);
//        }
//
//        // WHERE 조건들 연결
//        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
//        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
//
//        return query.getResultList();

    }

    // OrderSimpleApiController.java ordersV3() -> 패치 조인용
    public List<Order> findAllWithMemberDelivery() {
        // 패치 조인
        // 먼저 조인을 세팅을 하고, 한 번에 가져온다.
        // 이 경우 Order.java의 Member와 Delivery가 Lazy이지만 무시하고
        // 진짜 객체를 생성해서 값을 채워야 리턴한다.
        return em.createQuery("select o from Order o" +
                " join fetch o.member m " +
                " join fetch o.delivery d ", Order.class
        ).getResultList();

    }

    // OrderApiController.java ordersV3() -> 컬렉션 패치 조인용
    // 컬렉션 패치 조인
    // 문제 : Order 데이터가 OrderItem 만큼 데이터가 많아진다(뻥튀기)
    // 해결책 : OrderRepository.java findAllWithItem()의 distinct 추가 (DB의 distinct 기능 + JPA가 같은 참조값이면, 즉 엔티티가 중복인 경우에 중복제거 후 컬렉션에 담는다)
    // 그러나 최대 단점 : 1:多  관계에서 패치 조인을 할 경우, 페이징 불가능하다!
    // WARN 38084 --- [nio-8080-exec-2] o.h.h.internal.ast.QueryTranslatorImpl   : HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
    // 패치 조인을 사용했는데, 페이징 쿼리가 요청? 그럼 메모리 상에서 sort 처리:( -> 데이터가 만 개 이상인 경우, 메모리에 만 개를 다 올린 후, 페이징 처리 -> 메모리 과부하 가능성 높음(OUT OF MEMORY)
    // 기본적으로 패치 조인시 Order 데이터가 많아 지는데, 이를 가지로 페이징 처리를 하려고 하니, 기준점 자체가 흔들려서 위와 같은 현상이 발생함.
    public List<Order> findAllWithItem() {
        return em.createQuery("select distinct o from Order o " +
                              " join fetch o.member m" +
                              " join fetch o.delivery d" +
                              " join fetch o.orderItems oi" +
                              " join fetch oi.item i", Order.class)
                               // 아래부터는 페이징 쿼리
                               .setFirstResult(1)
                               .setMaxResults(100)
                               .getResultList();

    }

    // OrderApiController.java  ordersV3_page() -> 컬렉션 + 페이징
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m " +
                " join fetch o.delivery d ", Order.class
        ).setFirstResult(offset).setMaxResults(limit).getResultList();

    }
}
