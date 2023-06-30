package com.shop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("상품 등록 페이지 권한 테스트")
	// 현재 회원의 이름이 admin이고, role이 ADMIN인 유저가 로그인된 상태로 테스트할 수 있도록 해주는 어노테이션
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void itemFormTest() throws Exception {
		// 상품 등록 페이지에 get 요청을 보낸다.
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
				// 요청과 응답 메시지를 확인할 수 있도록 콘솔창에 출력해준다.
				.andDo(print())
				// 응답 상태 코드가 정상인지 확인한다.
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("상품 들록 페이지 일반 회원 접근 테스트")
	// 현재 인증된 사용자의 Role을 USER로 세팅한다.
	@WithMockUser(username = "user", roles = "USER")
	public void itemFormNotAdminTest() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
				.andDo(print())
				// 상품 등록 페이지 진입 요청 시 Forbidden 예외가 발생하면 테스트가 성공적으로 통과한다.
				.andExpect(status().isForbidden());
	}
}
