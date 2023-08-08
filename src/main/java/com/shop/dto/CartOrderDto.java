package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class CartOrderDto {

	private Long cartItemId;

	// 장바구니에서 여러 개의 상품을 주문하므로 CartOrderDto 클래스가 자기 자신을 List로 가지고 있도록 만든다.
	private List<CartOrderDto> cartOrderDtoList;
}
