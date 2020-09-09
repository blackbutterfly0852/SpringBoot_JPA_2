package jpabook.jpashop.domain.Item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("M")
public class Movie extends Item{

    private String author;
    private String isbn;

    @Override
    public void changeItem(int stockQuantity, String author, String isbn) {

    }
}
