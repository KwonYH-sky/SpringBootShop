package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart extends BaseEntity {

	@Id
	@Column(name = "cart_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY) // @OneToOne 어노테시션을 이용해 회원 엔티티와 일대일 매핑을 한다.
	// @JoinColumn 어노테시션을 이용해 매핑할 외래키를 지정한다.
	// name 속성에는 매핑할 외래키의 이름을 설정한다.
	// @JoinColumn의 name을 명시하지않으면 JPA가 알아서 ID를 찾지만 컬럼명이 원하는 대로 생성되지 않을 수 있어 직접 지정
	@JoinColumn(name = "member_id")
	private Member member;

	public static Cart createCart(Member member) {
		Cart cart = new Cart();
		cart.setMember(member);
		return cart;
	}
}
