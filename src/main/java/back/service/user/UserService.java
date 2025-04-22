package back.service.user;

import org.apache.ibatis.session.SqlSession;

import back.model.user.User;

public interface UserService {

	public boolean checkUserIdDuplicate(User user);
	 
    public boolean registerUser(User user);

    public boolean validateUser(User user);

    public User getUserById(String userId);
    
    public boolean updateUser(User user);
    
    public boolean deleteUser(User user);
    
    
}
