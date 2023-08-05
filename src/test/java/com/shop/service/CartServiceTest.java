package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.CartItemDto;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartServiceTest {

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CartService cartService;

	@Autowired
	CartItemRepository cartItemRepository;

	// 장바구니에 담을 상품 저장하는 메소드
	public Item saveItem() {
		Item item  = new Item();
		item.setItemNm("테스트 상품");
		item.setPrice(10000);
		item.setItemDetail("테스트 상품 상세 설명");
		item.setItemSellStatus(ItemSellStatus.SELL);
		item.setStockNumber(100);
		return itemRepository.save(item);
	}

	// 회원 정보 저장하는 메소드
	public Member saveMember() {
		Member member = new Member();
		member.setEmail("test@test.com");
		return memberRepository.save(member);
	}

	@Test
	@DisplayName("장바구니 담기 테스트")
	public void addCart() {
		Item item = saveItem();
		Member member = saveMember();

		CartItemDto cartItemDto = new CartItemDto();
		cartItemDto.setCount(5);
		cartItemDto.setItemId(item.getId());

		// 상품을 장바구니에 담는 로직 호출 결과 생성된 장바구니 상품 아이디를 cartItemId 변수에 저장
		Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());

		// 장바구니 상품 아이디를 이용하여 생성된 장바구니 상품을 조회한다.
		CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

		assertEquals(item.getId(), cartItem.getItem().getId()); // 상품 아이디와 장바구니에 저장된 상품 아이디가 같다면 테스트 통과
		assertEquals(cartItemDto.getCount(), cartItem.getCount()); // 정바구니에 담았던 수량과 실제로 장바구니에 저장된 수량이 같다면 테스트 통과
	}
}
