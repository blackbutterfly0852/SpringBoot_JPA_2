package jpabook.jpashop.domain.Item;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("B")
@Slf4j
public class Book extends Item{

    private String author;
    private String isbn;

    // 200908 book 생성 메서드 개인적으로 추가 -> ItemController에서 사용
    public Book(){

    }

    public static Book createBook(String name, int price, int stockQuantity, String author, String isbn){
        log.info("createBook method");
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        book.setAuthor(author);
        book.setIsbn(isbn);
        return book;
    }

//    public static Book updateBook(long id, String name, int price, int stockQuantity, String author, String isbn){
//        Book book = new Book();
//        book.setId(id);
//        book.setName(name);
//        book.setPrice(price);
//        book.setStockQuantity(stockQuantity);
//        book.setAuthor(author);
//        book.setIsbn(isbn);
//        return book;
//    }

    // 200909 변경감지 update를 위한 메소드 개인적으로 추가
    @Override
    public void changeItem(int stockQuantity, String author, String isbn) {
        log.info("changeItem method");
        this.setStockQuantity(stockQuantity);
        this.setAuthor(author);
        this.setIsbn(isbn);
    }
}
