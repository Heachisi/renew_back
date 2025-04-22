package back.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import back.model.user.User;

@Mapper
public interface UserMapper {
	
	public int checkUserIdDuplicate(User user);
	
    public int registerUser(User user);

    public User getUserById(String userId);

    public int updateUser(User user);

    public int deleteUser(User user);
    
    
}