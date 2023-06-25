package com.shop.controller;


import com.shop.dto.MemberFormDto;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원 가입 페이지로 이동할 수 있도록 {@link MemberController} 클래스에 메소드를 작성한다.
	 */
	@GetMapping(value = "/new")
	public String memberForm(Model model) {
		model.addAttribute("memerFormDto", new MemberFormDto());
		return "member/memberForm";
	}
}
