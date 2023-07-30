package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

	private final ItemRepository itemRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final ItemImgRepository itemImgRepository;

	public Long order(OrderDto orderDto, String email) {
		Item item = itemRepository.findById(orderDto.getItemId()) // 주문할 상품을 조회한다.
				.orElseThrow(EntityNotFoundException::new);
		Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원의 이메일 정보를 이용해서 회원 정보를 조회한다.

		List<OrderItem> orderItemList = new ArrayList<>();
		// 주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티를 생성한다.
		OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

		orderItemList.add(orderItem);

		// 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성한다.
		Order order = Order.createOrder(member, orderItemList);
		orderRepository.save(order); // 생성한 주문 엔티티를 저장한다.

		return order.getId();
	}

	@Transactional(readOnly = true)
	public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

		List<Order> orders = orderRepository.findOrders(email, pageable); // 유저의 아이디와 페이징 조건을 이용하여 주문 목록을 조회
		Long totalCount = orderRepository.countOrder(email); // 유저의 주문 총 개수를 구한다.

		List<OrderHistDto> orderHistDtos = new ArrayList<>();

		for (Order order : orders) { // 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO를 생성한다.
			OrderHistDto orderHistDto = new OrderHistDto(order);
			List<OrderItem> orderItems = order.getOrderItems();
			for (OrderItem orderItem : orderItems) {
				// 주문한 상품의 대표 이미지를 조회한다.
				ItemImg itemImg = itemImgRepository.findByIdAndRepimgYn(orderItem.getItem().getId(), "Y");
				OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
				orderHistDto.addOrderItemDto(orderItemDto);
			}

			orderHistDtos.add(orderHistDto);
		}
		// 페이지 구현 객체를 생성하여 반환한다.
		return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
	}
}
