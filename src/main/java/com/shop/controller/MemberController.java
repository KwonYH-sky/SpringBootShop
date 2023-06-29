package com.shop.controller;


import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원 가입 페이지로 이동할 수 있도록 {@link MemberController} 클래스에 메소드를 작성한다.
	 */
	@GetMapping(value = "/new")
	public String memberForm(Model model) {
		model.addAttribute("memberFormDto", new MemberFormDto());
		return "member/memberForm";
	}

//	@PostMapping(value = "/new")
//	public String memberForm(MemberFormDto memberFormDto) {
//		Member member = Member.createMember(memberFormDto, passwordEncoder);
//		memberService.saveMember(member);
//
//		return "redirect:/";
//	}

	/**
	 * 검증하려는 객체의 앞에 @Valid 어노테이션을 선언하고, 파라미터로 bindingResult 객체를 추가한다.
	 * 검사 후 결과는 bindingResult에 담아준다.
	 */
	@PostMapping(value = "/new")
	public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
		// bindingResult.hasErrors()를 호출하여 에라가 있다면 회원가입 페이지로 이동한다.
		if (bindingResult.hasErrors()) {
			return "member/memberForm";
		}

		try {
			Member member = Member.createMember(memberFormDto, passwordEncoder);
			memberService.saveMember(member);
		} catch (IllegalStateException e) {
			// 회원 가입 시 중복 회원 가입 예외가 발생하면 에러 메시지를 뷰로 전달한다.
			model.addAttribute("errorMessage", e.getMessage());
			return "member/memberForm";
		}

		return "redirect:/";
	}

	@GetMapping(value = "/login")
	public String loginMember() {
		return "/member/memberLoginForm";
	}

	@GetMapping(value = "/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
		return "/member/memberLoginForm";
	}
}
