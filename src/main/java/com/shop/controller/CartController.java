package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping(value = "/cart")
	public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal) {

		// 장바구니에 담을 상품 정보를 받는 cartItemDto 객체에 데이터 바인딩 시 에러가 있는지 검사한다.
		if (bindingResult.hasErrors()){
			StringBuilder sb = new StringBuilder();
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();

			for (FieldError fieldError : fieldErrors)
				sb.append(fieldError.getDefaultMessage());

			return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
		}
		// 현재 로그인한 회원의 이메일 정보를 변수에 저장한다.
		String email = principal.getName();
		Long cartItemId;

		try {
			// 화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일 정보를 이용하여 장바구니에 상품을 담는 로직을 호출
			cartItemId = cartService.addCart(cartItemDto, email);
		} catch (Exception e){
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		// 결과 값으로 생성된 장바구니 상품 아이디와 요청이 성공하였다는 HTTP 응답 상태 코드를 반환한다.
		return new ResponseEntity(cartItemId, HttpStatus.OK);
	}

	@GetMapping(value = "/cart")
	public String orderHist(Principal principal, Model model) {
		// 현재 로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품 정보를 조회한다.
		List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());
		// 조회한 장바구니 상품 정보를 뷰로 전달한다.
		model.addAttribute("cartItems", cartDetailDtoList);
		return "cart/cartList";
	}
}
