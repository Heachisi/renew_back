package back.controller.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.model.common.CustomUserDetails;
import back.model.user.User;
import back.service.user.UserService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    /**
     * 사용자 조회
     */
    @PostMapping("/view.do")
    public ResponseEntity<?> view() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        SecurityUtil.checkAuthorization(userDetails, userDetails.getUser().getUserId());
        User user = userService.getUserById(userDetails.getUser().getUserId());

        return ResponseEntity.ok(new ApiResponse<>(true, "조회 성공", user));
    }
    
    /**
     * 아이디 중복 확인
     */
    @PostMapping("/checkUserId.do")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserId(@RequestBody User user) {
        log.info("아이디 중복 검사 : {}", user.getUserId());

        Map<String, Object> data = new HashMap<>();
        boolean exists = userService.checkUserIdDuplicate(user);
        data.put("exists", exists); // 

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "중복체크 성공",
            data
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입
     */
    @PostMapping("/register.do")
    public ResponseEntity<?> register(@RequestBody User user) {
        log.info("회원가입 요청 : {}", user.getUserId());
        user.setCreateId("SYSTEM");
        boolean success = userService.registerUser(user);

        return ResponseEntity.ok(new ApiResponse<>(success, success ? "회원가입 성공" : "회원가입 실패", null));
    }


    /**
     * 회원정보 수정
     */
    @PostMapping("/update.do")
    public ResponseEntity<?> update(@RequestBody User user) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        log.info("회원정보 수정 요청: {}", user.getUserId());
        SecurityUtil.checkAuthorization(userDetails, user.getUserId());
        user.setUpdateId(userDetails.getUsername());

        boolean success = userService.updateUser(user);

        return ResponseEntity.ok(new ApiResponse<>(success, success ? "수정 성공" : "수정 실패", null));
    }

    @PostMapping("/delete.do")
    public ResponseEntity<?> delete(@RequestBody User user, HttpSession session) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        log.info("회원탈퇴 요청: {}", user.getUserId());
        SecurityUtil.checkAuthorization(userDetails, user.getUserId());
        user.setUpdateId(userDetails.getUsername());

        boolean success = userService.deleteUser(user);
        if (success) {
            session.invalidate();
            SecurityContextHolder.clearContext();
        }

        return ResponseEntity.ok(new ApiResponse<>(success, success ? "삭제 성공" : "삭제 실패", null));
    }
    @PostMapping("/login.do")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
        log.info("로그인 시도 : {}", user.getUserId());
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );
            log.info("세션 ID: {}", session.getId());

            return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", null));
        } catch (AuthenticationException e) {
            log.warn("로그인 실패 : {}", user.getUserId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "아이디 또는 비밀번호가 일치하지 않습니다.", null));
        }
    }

    @PostMapping("/logout.do")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("로그아웃 요청");

        request.getSession().invalidate();
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new ApiResponse<>(true, "로그아웃 성공", null));
    }
    
    
    
    
}
