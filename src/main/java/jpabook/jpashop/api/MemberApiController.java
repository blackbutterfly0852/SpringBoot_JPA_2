// C:\200821 Spring_Boot_1\h2-2019-03-13\h2\bin
package jpabook.jpashop.api;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // API는 JSON으로 통신,
    // @Request(header)Body는 Json 데이터를 Member 엔티티로 바인딩
    // 1. 회원등록_1
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 1. 회원등록_2
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }


    // 2. 회원수정
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2
    (@PathVariable("id") Long id,
     @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id); // 커맨드와 쿼리를 분리하는 스타일.
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    // 3. 회원조회
    // v1의 단점 : 회원조회인데 주문 내역까지 전달 -> Member.java에서 @JsonIgnore

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    // 3. 회원조회_2
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream().map((m -> new MemberDto(m.getName()))).collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    // DTO
    // 1. 회원등록
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    // 2. 회원수정
    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    // 3. 회원조회
    @Data
    @AllArgsConstructor
    // 한 번 감싸서 반환했기 때문에, JSON의 확장 유연성이 높아진다.
    static class Result<T> {
        private int count;
        private T data;
    }
    @Data
    @AllArgsConstructor
    // 필요한 데이터만 넘겨야 된다.
    // DTO를 추가적으로 생성했지만, 엔티티 스펙이 변해도, API에 영향을 주지 않는다.
    static class MemberDto {
        String name;
    }


}
