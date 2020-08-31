package jpabook.jpashop.domain;

import jpabook.jpashop.domain.Item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;
    private String name;

    @ManyToMany
    // RDB 때문에 필요 // 실무에서는 @ManyToMany X -> 중간 테이블에 컬럼 추가 불가
    @JoinTable(name = "category_item", // 중간 테이블 명
               joinColumns = @JoinColumn (name = "category_id"), //  category 테이블 부터 출발하여 FK
               inverseJoinColumns = @JoinColumn(name = "item_id") // Item 테이블로 가기위한 FK
    )
    private List<Item> items = new ArrayList<Item>();


    // SELF로 양방향 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<Category>();

    // 연관관계 메서드
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);

    }
}
