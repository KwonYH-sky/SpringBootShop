package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원 가입 회면으로부터 넘어오는 가입정보
 */
@Getter @Setter
public class MemberFormDto {
	private String name;
	private String email;
	private String password;
	private String address;
}
