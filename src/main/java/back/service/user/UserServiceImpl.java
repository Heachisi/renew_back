package back.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import back.exception.HException;
import back.mapper.user.UserMapper;
import back.model.user.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    
    @Override
    @Transactional
    public boolean checkUserIdDuplicate(User user) {
        try {
            int count = userMapper.checkUserIdDuplicate(user);
            return count > 0;
        } catch (Exception e) {
            log.error("아이디 중복 체크 중 오류 발생!", e);
            throw new HException("아이디 중복 체크 실패", e);
        }
    }

    
    @Override
    public boolean registerUser(User user) {
        try {
            String password = user.getPassword();
            user.setPassword(password != null ? passwordEncoder.encode(password) : null);
            return userMapper.registerUser(user) > 0;
        } catch (Exception e) {
            log.error("회원가입 중 오류", e);
            throw new HException("회원가입 실패", e);
        }
    }

    @Override
    public boolean validateUser(User user) {
        try {
            User dbUser = userMapper.getUserById(user.getUserId());
            if (dbUser == null) return false;

            return passwordEncoder.matches(user.getPassword(), dbUser.getPassword());
        } catch (Exception e) {
            log.error("로그인 중 오류", e);
            throw new HException("로그인 중 오류", e);
        }
    }

    @Override
    public User getUserById(String userId) {
        try {
            return userMapper.getUserById(userId);
        } catch (Exception e) {
            log.error("사용자 조회 중 오류", e);
            throw new HException("사용자 조회 실패", e);
        }
    }
    
    @Override
    public boolean updateUser(User user) {
        try {
        String password = user.getPassword();
        user.setPassword(password!=null ? passwordEncoder.encode(password):null);
             return userMapper.updateUser(user) > 0;
        } catch (Exception e) {
              log.error("사용자 수정 중 오류",e);
            throw new HException("사용자 수정 실패",e);
        }
  }

	@Override
	public boolean deleteUser(User user) {
		try {
	        String password = user.getPassword();
	        user.setPassword(password!=null ? passwordEncoder.encode(password):null);
	             return userMapper.deleteUser(user) > 0;
	        } catch (Exception e) {
	              log.error("사용자 탈퇴 중 오류",e);
	            throw new HException("사용자 탈퇴 실패",e);
	        }
	}
    
    
 
    
}