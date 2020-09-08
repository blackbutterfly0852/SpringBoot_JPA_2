package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }


    @PostMapping("/items/new")
    public String create(BookForm form){
        // 아래 set 설계 보다는 static createBook() 통해 생성하는 것이 바람직한 설계
        // 실무에서는 set을 안열어두기 때문
        /*Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());*/
        Book book = Book.createBook(form.getName(),form.getPrice(),form.getStockQuantity(),form.getAuthor(),form.getIsbn());
        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String findAll(Model model){
        List<Item> itemList = itemService.findItems();
        model.addAttribute("items", itemList);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book book = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setName(book.getName());
        form.setPrice(book.getPrice());
        form.setAuthor(book.getAuthor());
        form.setIsbn(book.getIsbn());
        form.setStockQuantity(book.getStockQuantity());

        model.addAttribute("form", book);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    // 여기선 @PathVariable String itemId 필수조건 X
    // @ModelAttribute("form") 기본이 form 객체로 넘어온다
    // id값 유의 해당 User가 해당 Item에 대한 수정 권한 있는지 체크
    public String updateItem(@PathVariable String itemId ,@ModelAttribute("form") BookForm form){
        Book book =  Book.updateBook(form.getId(), form.getName(),form.getPrice(),form.getStockQuantity(),form.getAuthor(),form.getIsbn());
        itemService.saveItem(book);
        return "redirect:/items";
    }




}
