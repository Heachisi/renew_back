package back.service.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.exception.HException;
import back.mapper.file.FileMapper;
import back.model.common.PostFile;
import back.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
	private FileMapper fileMapper;

	@Override
	@Transactional
	public Map<String, Object> insertBoardFiles(PostFile file) {
	    Map<String, Object> resultMap = new HashMap<String, Object>();

	    try {
	        int boardId = file.getBoardId();
	        String userId = file.getCreateId();
	        String basePath = file.getBasePath();

	        List<MultipartFile> files = file.getFiles();

	        if (files == null || files.isEmpty()) {
	            resultMap.put("result", false);
	            resultMap.put("message", "파일이 존재하지 않습니다.");
	            return resultMap;
	        }

	        List<PostFile> uploadedFiles = FileUploadUtil.uploadFiles(files, basePath, boardId, userId);

	        for (PostFile postfile : uploadedFiles) {
	        	log.info(postfile.toString());
	            fileMapper.insertFile(postfile);
	        }

	        resultMap.put("result", true);
	        if(uploadedFiles != null && uploadedFiles.size() > 0) {
	            resultMap.put("fileId", uploadedFiles.get(0).getFileId());
	        }

	        return resultMap;
	    } catch (Exception e) {
	        log.error("파일 업로드 중 오류", e);
	        throw new HException("파일 업로드 실패", e);
	    }
	}
	
	@Override
	public PostFile getFileByFileId(PostFile file) {
		 return fileMapper.getFileByFileId(file);
	}



	
    

    
    
    
}