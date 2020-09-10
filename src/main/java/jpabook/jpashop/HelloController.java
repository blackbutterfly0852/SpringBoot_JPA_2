package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model){ // model에 data 담은 후 view로 넘김
        model.addAttribute("data","hello!!");
        return "hello"; // view 이름 ex) hello.html
    }
}
