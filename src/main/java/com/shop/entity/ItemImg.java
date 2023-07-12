package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "item_img")
@Getter @Setter
public class ItemImg extends BaseEntity {

	@Id
	@Column(name = "item_img_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String imgName; // 이미지 파일명

	private String oriImgName; // 원본 이미지 파일명

	private String imgUrl; // 이미지 조회 경로

	private String repimgYn; // 대표 이미지 여부

	// 상품 엔티티와 다대일 단방향 관계로 매핑한다.
	// 지연 로딩을 설정하여 매핑된 상품 엔티티 정보가 필요한 경우 데이터를 조회하도록 한다.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	/**
	 * 원본 이미지 파일명, 업데이트할 이미지 파일명, 이미지 경로를 파라미터로 입력 받아서 이미지 정보를 업데이트 하는 메소드 
	 */
	public void updateItemImg(String oriImgName, String imgName, String imgUrl) {
		this.oriImgName = oriImgName;
		this.imgName = imgName;
		this.imgUrl = imgUrl;
	}

}
