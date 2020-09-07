package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // model : Controller에서 View로 넘어갈 때 유효성 체크를 위해 new MemberForm()를 전달.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    // MemberForm.java vs Member.java : Controller에서 넘어오는 유효성과 도메인이 원하는 유효성은 다를 수 있기 때문에 별도로 Form 객체를 생성
    public String create(@Valid MemberForm form, BindingResult result) { // @Valid : MemberForm의 'name' 유효성 확인, 유효성 통과를 못하면 에러가 BindingResult에 담김.
        if (result.hasErrors()) {
            return "members/createMemberForm"; // 에러를 담아 View로 전달 (createMemberForm.html의 <label th:for="name">이름</label>) 해당 부분 확인
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers(); // 실무에서는 꼭 필요한 데이터만 가공해서 View로 전달
        model.addAttribute("members", members);
        return "members/memberlist";
    }
}
