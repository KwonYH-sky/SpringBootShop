package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table
@Getter @Setter
@ToString
public class Member extends BaseEntity{

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@Column(unique = true) // 동일한 이메일 값이 데이터베이스에 들어올 수 없도록 unique 속성을 지정한다.
	private String email;

	private String password;

	private String address;

	/**
	 * 자바의 enum 타입을 엔티티 속성으로 지정할 수 있다.
	 * Enum을 사용할 때 기본적으로 순서가 저장되는데, enum의 순서가 바뀔 경우 문제가 발생핼 수 있으므로
	 * "EnumType.STRING"옵션을 사용해서 String으로 저장하는 것을 권장된다.
	 */
	@Enumerated(EnumType.STRING)
	private Role role;

	/**
	 * Member 엔티티를 생성하는 메소드이다.
	 * {@link Member} 엔티티에 회원을 생성하는 메소드를 만들어서 관리한다면 코드가 변경되더라도 한 군데만 수정하면 되는 이점이 있다.
	 */
	public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
		Member member = new Member();
		member.setName(memberFormDto.getName());
		member.setEmail(memberFormDto.getEmail());
		member.setAddress(memberFormDto.getAddress());
		String password = passwordEncoder.encode(memberFormDto.getPassword()); // 스프링 시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호를 암호화 한다.
		member.setPassword(password);
		member.setRole(Role.ADMIN);
		return member;
	}
}
