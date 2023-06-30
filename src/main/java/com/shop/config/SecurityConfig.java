package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * {@link WebSecurityConfigurerAdapter}을 상속 받는 클레스에 @EnableWebSecurity 어노테이션을 선언하면 SpringSecurityFilterChain이 자동으로 포함된다.
 * WebSecurityConfigurerAdapter를 상속받아서 메소드 오버라이딩을 통해 보안 설정을 커스텀마이징 할 수 있다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	MemberService memberService;

	/**
	 * http 요청에 대한 보안을 설정한다.
	 * 페이지 권한 설정, 로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정을 작성한다.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin()
				// 로그인 페이지 URL을 설정한다.
				.loginPage("/members/login")
				// 로그인 성공 시 이동할 URL을 설정한다.
				.defaultSuccessUrl("/")
				// 로그인 시 사용할 파라미터 이름으로 email을 지정한다.
				.usernameParameter("email")
				// 로그인 실패 시 이동할 URL을 설정한다.
				.failureUrl("/members/login/error")
				.and()
				.logout()
				// 로그아웃 URL을 설정한다.
				.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
				// 로그아웃 성공 시 이동할 URL을 설정한다.
				.logoutSuccessUrl("/");

		http.authorizeRequests() // 시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
				// permitAll()을 통해 모든 사용자가 인증(로그인)없이 해당 결로에 접근할 수 있도록 설정한다.
				// 메인 페이지, 회원 관련 URL, 상품 상세페이지, 상품 이미지를 불러오는 경로가 이에 해당한다.
				.mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
				// /admin으로 시작하는 경로는 해당 계정이 ADMIN Role일 경우에만 접근 가능하도록 설정한다.
				.mvcMatchers("/admin/**").hasRole("ADMIN")
				// 앞서 설정해준 경로를 제외한 나머지 경로는 모두 인증을 요구하도록 설정한다.
				.anyRequest().authenticated();

		// 인증되지 않는 사용자가 리소스에 접근하였을 때 수행되는 핸들러를 등록한다.
		http.exceptionHandling()
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint());

	}

	/**
	 * 비밀번호를 데이터베이스에 그대로 저장했을 경우, 데이터베이스가 해킹당하면 고객의 회원 정보가 그대로 노출된다.
	 * 이를 해결하기 위해 {@link BCryptPasswordEncoder}의 해시 함수를 이용하여 비밀번호를 암호화하여 저장한다.
	 * BCryptPasswordEncoder를 빈으로 등록하여 사용한다.
	 */
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	/**
	 * Spring Security에서 인증은 AuthenticationManager를 통해 이루어지며
	 * {@link AuthenticationManagerBuilder}가 생성된다.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// userDetailsService를 구현하고 있는 객체로 memberService를 지정해주며, 비밀번호 암호화를 위해 passwordEncoder를 지정해준다.
		auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// static 디렉터리의 하위 파일은 인증을 무시하도록 설정한다.
		web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
	}
}
