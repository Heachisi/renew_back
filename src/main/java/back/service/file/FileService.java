package back.service.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import back.model.common.PostFile;

public interface FileService {
	
	
    public PostFile getFileByFileId(PostFile file);
    
    public Map<String, Object> insertBoardFiles(PostFile file);
		

}
