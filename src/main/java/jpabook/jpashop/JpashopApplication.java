package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {

	    Hello hello = new Hello();
	    hello.setData("hello");
	    String data = hello.getData();
	    System.out.println("data : " + data);

		SpringApplication.run(JpashopApplication.class, args);
	}


	@Bean
	Hibernate5Module hibernate5Module(){
		// 기본설정 : Lazy 로딩 필드는 프록시 객체인 경우 무시.
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		// Lazy 로딩 필드도 조회 -> 불필요한 정보 노출, 성능이슈, API 스펙변경리스크
		// 아래 커맨드를 주석 처리하고, OrderSimpleApiController.java에서 For 구문 사용 하면 원하는 데이터만 출력 가능
		//  hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING,true);
		return new Hibernate5Module();
	}

}
