package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CartDetailDto {

	private Long cartItemId;

	private String itemNm;

	private int price;

	private int count;

	private String imgUrl;

	/**
	 * 장바구니 페이지에 전달할 데이터를 생성자의 파라미터로 만들어준다.
	 * @param cartItemId
	 * @param itemNm
	 * @param price
	 * @param count
	 * @param imgUrl
	 */
	public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl) {
		this.cartItemId = cartItemId;
		this.itemNm = itemNm;
		this.price = price;
		this.count = count;
		this.imgUrl = imgUrl;
	}
}
