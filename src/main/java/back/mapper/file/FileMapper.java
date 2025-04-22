package back.mapper.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import back.model.common.PostFile;

@Mapper
public interface FileMapper {

    // 게시판 첨부 파일 저장
    public int insertFile(PostFile file);

    // 파일 ID로 첨부 파일 조회
    public PostFile getFileByFileId(PostFile file);

    // 게시판 ID로 첨부 파일 목록 조회
    public List<PostFile> getFilesByBoardId(String boardId);

    // 게시판 첨부 파일 삭제 (DEL YN = 'Y' 처리)
    public int deleteFile(PostFile file);
}