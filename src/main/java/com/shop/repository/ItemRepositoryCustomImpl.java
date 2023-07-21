package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom { // ItemRepositoryCustom을 상속받는다.

	private JPAQueryFactory queryFactory; // 동적으로 쿼리를 생성하기 위해서 JPAQueryFactory 클래스를 사용한다.

	public ItemRepositoryCustomImpl(EntityManager em) { // JPAQueryFactory의 생성자로 EntityManager 객체를 넣어준다.
		this.queryFactory = new JPAQueryFactory(em);
	}

	/**
	 * 상품 판매 상태 조건이 전체(null)일 경우는 null을 리턴한다.
	 * 결과값이 null이면 where 절에서 해당 조건은 무시된다.
	 * 상품 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회한다.
	 */
	private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
		return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
	}

	/**
	 * searchDateType의 값에 따라서 dateTime의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회한다.
	 * 예를 들어 searchDateType 값이 "1m"인 경우 dateTime의 시간을 한 달 전으로 세팅 후
	 * 최근 한 달동안 등록된 상품만 조회하도록 조건값을 반환한다.
	 */
	private BooleanExpression regDtsAfter(String searchDateType) {
		LocalDateTime dateTime = LocalDateTime.now();

		if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
			return null;
		} else if (StringUtils.equals("1d", searchDateType)) {
			dateTime = dateTime.minusDays(1);
		} else if (StringUtils.equals("1w", searchDateType)) {
			dateTime = dateTime.minusWeeks(1);
		} else if (StringUtils.equals("1m", searchDateType)) {
			dateTime = dateTime.minusMonths(1);
		} else if (StringUtils.equals("6m", searchDateType)) {
			dateTime = dateTime.minusMonths(6);
		}

		return QItem.item.regTime.after(dateTime);
	}

	/**
	 * searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품
	 * 또는 상품 생성자의 아이디에 검색어에 포함하고 있는 상품을 조회하도록 조건값을 반환한다.
	 */
	private BooleanExpression searchByLike(String searchBy, String searchQuery) {

		if (StringUtils.equals("itemNm", searchBy)) {
			return QItem.item.itemNm.like("%" + searchQuery + "%");
		} else if (StringUtils.equals("createBy", searchBy)) {
			return QItem.item.createBy.like("%" + searchQuery + "%");
		}

		return null;
	}

	/**
	 * queryFactory를 이용해서 쿼리를 생성한다.
	 * 쿼리문을 직접 작성할 때의 형태와 문법이 비슷한 것을 볼 수 있다.
		 * selectFrom(QItem.item): 상품 데이터를 조회하기 위해서 {@link QItem}의 item을 지정한다.
		 * where 조건절: BooleamExpresstion 반환하는 조건문들을 넣어준다. ',' 단위로 넣어줄 경우 and 조건으로 인식한다.
		 * offset: 데이터를 가지고 올 시작 인덱스를 지정한다.
		 * limit: 한 번에 가져올 최대 개수를 지정한다.
		 * fetchResult(): 조회한 리스트 및 전체 개수를 포함하는 QueryResults를 반환한다.
	            상품 데이터 리스트 조회 및 상품 데이터 전체 개수를 조회하는 2번의 쿼리문이 실행
	 */
	@Override
	public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

		QueryResults<Item> results = queryFactory
				.selectFrom(QItem.item)
				.where(regDtsAfter(itemSearchDto.getSearchDateType()),
						searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
						searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
				.orderBy(QItem.item.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();

		List<Item> content = results.getResults();
		long total = results.getTotal();
		return new PageImpl<>(content, pageable, total); // 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 반환한다.
	}
}
