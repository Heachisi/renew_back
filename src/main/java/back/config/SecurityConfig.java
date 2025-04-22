package back.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;

import back.service.common.CustomUserDetailsService;
import back.util.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 🔐 Spring Security 설정 클래스
 * 
 * ✔ 세션 기반 인증 방식 사용 (프론트엔드 분리 구조 대응)
 * ✔ JSON 기반 API 요청 처리 (폼 로그인 X)
 * ✔ CORS 설정 포함 (withCredentials + 세션 쿠키 허용)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * ✅ 사용자 인증 관련 설정
     * - CustomUserDetailsService + 비밀번호 인코더 등록
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    /**
     * ✅ 보안 필터 체인 정의
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            /**
             * ✅ CORS 설정 적용
             * - WebConfig의 WebMvcConfigurer에서 정의한 CORS 정책을 활성화
             * - withCredentials(true)를 위해 꼭 필요함!
             */
        	.cors(Customizer.withDefaults()) 

            /**
             * ✅ CSRF 비활성화
             * - 프론트엔드가 JSON으로 요청을 보낼 경우 보통 비활성화
             */
            .csrf(csrf -> csrf.disable())

            /**
             * ✅ 요청 경로별 인증/인가 정책 정의
             */
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/user/login.do",
                    "/api/user/logout.do",
                    "/api/user/register.do",
                    "/api/user/checkUserId.do",
                    "/api/file/down.do",
                    "/api/file/imgDown.do",
                    "/api/file/imgUpload.do"
                    
                ).permitAll() // 로그인, 로그아웃, 회원가입은 누구나 접근 가능
                .anyRequest().authenticated() // 그 외는 인증 필요
            )

            /**
             * ✅ 로그인 방식 설정
             * - formLogin: 사용 안 함 (프론트에서 JSON으로 처리)
             * - httpBasic: 디버깅용 기본 인증 (필요 없으면 나중에 제거 가능)
             */
            .formLogin(form -> form.disable())
            .httpBasic(Customizer.withDefaults())

            /**
             * ✅ 세션 관리 정책
             * - IF_REQUIRED: 인증 시에만 세션 생성
             * - STATELESS: 토큰 기반 인증 시 사용 (지금은 X)
             */
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            /**
             * ✅ 인증 실패 시 처리 방식
             * - 인증되지 않은 사용자가 요청 시 JSON 형식의 401 응답 전송
             */
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                	 res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                     res.setContentType("application/json; charset=UTF-8");

                     ApiResponse<Object> apiResponse = new ApiResponse<>(false, "권한 없음", null);

                     // ApiResponse를 JSON으로 변환하여 응답
                     ObjectMapper objectMapper = new ObjectMapper();
                     String json = objectMapper.writeValueAsString(apiResponse);
                     res.getWriter().write(json);
                })
            );

        return http.build();
    }
}