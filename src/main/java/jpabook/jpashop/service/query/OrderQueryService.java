package jpabook.jpashop.service.query;

import org.springframework.transaction.annotation.Transactional;

// OSIV 성능 최적화
@Transactional(readOnly = true)
public class OrderQueryService {

}
