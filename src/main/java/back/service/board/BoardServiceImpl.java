package back.service.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import back.exception.HException;
import back.mapper.board.BoardMapper;
import back.mapper.file.FileMapper;
import back.model.board.Board;
import back.model.board.Comment;
import back.model.common.PostFile;
import back.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BoardServiceImpl implements BoardService {
    @Autowired
    private BoardMapper boardMapper;
    @Autowired
    private FileMapper fileMapper;

    @Override
    public List<Board> getBoardList(Board board) {
        try {
            int page = board.getPage();
            int size = board.getSize();

            int totalCount = boardMapper.getTotalBoardCount(board);
            int totalPages = (int) Math.ceil((double) totalCount / size);

            int startRow = (page - 1) * size + 1;
            int endRow = page * size;

            board.setTotalCount(totalCount);
            board.setTotalPages(totalPages);
            board.setStartRow(startRow);
            board.setEndRow(endRow);

            return boardMapper.getBoardList(board);
        } catch (Exception e) {
            log.error("게시물 목록 조회 실패", e);
            throw new HException("게시물 목록 조회 실패", e);
        }
    }


   
    @Override
    public Board getBoardById(String boardId) {
        try {
            Board board = boardMapper.getBoardById(boardId);
            board.setPostFiles(fileMapper.getFilesByBoardId(boardId));
            board.setComments(boardMapper.getCommentsByBoardId(boardId));
            return board;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("게시글 조회 실패", e);
            throw new HException("게시글 조회 실패", e);
        }
    }
    
    @Override
    @Transactional
    public boolean createBoard(Board board) {
        try {
            boolean result = boardMapper.create(board) > 0;
            List<MultipartFile> files = board.getFiles();
            if (result && files != null) {
                List<PostFile> fileList = FileUploadUtil.uploadFiles(files, "board",
                    Integer.parseInt(board.getBoardId()), board.getCreateId());
                for (PostFile postFile : fileList) {
                    boolean insertResult = fileMapper.insertFile(postFile) > 0;
                    if (!insertResult) throw new HException("파일 추가 실패");
                }
            }
            return result;
        } catch (Exception e) {
            log.error("게시글 등록 실패", e);
            throw new HException("게시글 등록 실패", e);
        }
    }
    @Override
    @Transactional
    public boolean updateBoard(Board board) {
        try {
            boolean result = boardMapper.update(board) > 0;

            if (result) {
                List<MultipartFile> files = board.getFiles();
                String remainingFileIds = board.getRemainingFileIds();

                List<PostFile> existingFiles = fileMapper.getFilesByBoardId(board.getBoardId());

                for (PostFile existing : existingFiles) {
                    if (!remainingFileIds.contains(String.valueOf(existing.getFileId()))) {
                        existing.setUpdateId(board.getUpdateId());
                        boolean deleteResult = fileMapper.deleteFile(existing) > 0;
                        if (!deleteResult) throw new HException("파일 삭제 실패");
                    }
                }

                if (files != null) {
                    List<PostFile> uploadedFiles = FileUploadUtil.uploadFiles(files, "board",
                            Integer.parseInt(board.getBoardId()), board.getUpdateId());
                    for (PostFile postFile : uploadedFiles) {
                        boolean insertResult = fileMapper.insertFile(postFile) > 0;
                        if (!insertResult) throw new HException("파일 추가 실패");
                    }
                }
            }

            return result;
        } catch (Exception e) {
            log.error("게시판 수정 실패", e);
            throw new HException("게시판 수정 실패", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteBoard(Board board) {
        try {
            return boardMapper.delete(board) > 0;
        } catch (Exception e) {
            log.error("게시글 삭제 실패", e);
            throw new HException("게시글 삭제 실패", e);
        }
    }

    @Override
    @Transactional
    public boolean createComment(Comment comment) {
        try {
            return boardMapper.insertComment(comment) > 0;
        } catch (Exception e) {
           log.error("댓글 등록 실패", e);
            throw new HException("댓글 등록 실패", e);
        }
    }

    @Override
    @Transactional
    public boolean updateComment(Comment comment) {
        try {
            return boardMapper.updateComment(comment) > 0;
        } catch (Exception e) {
            log.error("댓글 수정 실패", e);
            throw new HException("댓글 수정 실패", e);
        }
    }



	@Override
	@Transactional
	public boolean deleteComment(Comment comment) {
		 try {
	            return boardMapper.deleteComment(comment) > 0;
	        } catch (Exception e) {
	            log.error("댓글 수정 실패", e);
	            throw new HException("댓글 수정 실패", e);
	        }
	}
    
}