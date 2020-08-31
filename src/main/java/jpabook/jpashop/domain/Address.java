package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Getter
@Embeddable
// 값 타입은 변경이 되면 안됨, Setter 제공 X, 생성자 활용
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // JPA SPEC 상 기본생성자 필요, 변경 못하게 protected
    protected Address(){
    }

    public Address(String city, String street, String zipcode){
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
