package back.controller.board;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.model.board.Board;
import back.model.board.Comment;
import back.model.common.CustomUserDetails;
import back.service.board.BoardService;
import back.util.ApiResponse;
import back.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 게시판 관련 요청을 처리하는 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/board")
@Slf4j
public class BoardController {

    @Autowired
    private BoardService boardService;

    /**
     * 게시판 목록 조회
     */
    @PostMapping("/list.do")
    public ResponseEntity<?> getBoardList(@RequestBody Board board) {
        log.info(board.toString());
        List<Board> boardList = boardService.getBoardList(board);
        Map dataMap = new HashMap<>();
        dataMap.put("list", boardList);
        dataMap.put("board", board);
        return ResponseEntity.ok(new ApiResponse<>(true, "목록 조회 성공", dataMap));
    }

    /**
     * 게시판 단건 조회
     */
    @PostMapping("/view.do")
    public ResponseEntity<?> getBoard(@RequestBody Board board) {
        Board selectBoard = boardService.getBoardById(board.getBoardId());
        return ResponseEntity.ok(new ApiResponse<>(true, "조회 성공", selectBoard));
    }
    
    /**
     * 게시판 등록
     */
    @PostMapping(value = "/create.do", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBoard(
        @ModelAttribute Board board,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        SecurityUtil.checkAuthorization(userDetails);
        board.setCreateId(userDetails.getUsername());
        board.setFiles(files);
        boolean isCreated = boardService.createBoard(board);
        return ResponseEntity.ok(new ApiResponse<>(isCreated, isCreated ? "게시글 등록 성공" : "게시글 등록 실패", null));
    }
    
    /**
     * 게시판 수정
     */
    @PostMapping(value = "/update.do", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBoard(
        @ModelAttribute Board board,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        SecurityUtil.checkAuthorization(userDetails);
        board.setUpdateId(userDetails.getUsername());
        board.setFiles(files);
        boolean isUpdated = boardService.updateBoard(board);
        return ResponseEntity.ok(new ApiResponse<>(isUpdated, isUpdated ? "게시글 수정 성공" : "게시글 수정 실패", null));
    }
    

    /**
     * 게시판 삭제
     */
    
    @PostMapping("/delete.do")
    public ResponseEntity<?> deleteBoard(
        @ModelAttribute Board board,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        SecurityUtil.checkAuthorization(userDetails);
        board.setUpdateId(userDetails.getUsername());
        board.setFiles(files);
        boolean isDeleted = boardService.deleteBoard(board);
        return ResponseEntity.ok(new ApiResponse<>(isDeleted, isDeleted ? "게시글 수정 성공" : "게시글 수정 실패", null));
    }
    

    /**
     * 댓글 등록
     */
    
    @PostMapping("/comment/create.do")
    public ResponseEntity<?> createComment(@RequestBody Comment comment,
    		@AuthenticationPrincipal CustomUserDetails userDetails){
    	
    	SecurityUtil.checkAuthorization(userDetails);
    	comment.setCreateId(userDetails.getUsername());
    	boolean isCreated = boardService.createComment(comment);
    	return ResponseEntity.ok(new ApiResponse<>(isCreated, isCreated ? "댓글 등록 성공" : "댓글 등록 실패", null));
    }
    
    /**
     * 댓글 등록
     */

   @PostMapping("/comment/update.do")
   public ResponseEntity<?> updateComment(@RequestBody Comment comment,
		   @AuthenticationPrincipal CustomUserDetails userDetails){
	   
	   SecurityUtil.checkAuthorization(userDetails);
	   comment.setUpdateId(userDetails.getUsername());
   	boolean isUpdated = boardService.updateComment(comment);
   	return ResponseEntity.ok(new ApiResponse<>(isUpdated, isUpdated ? "댓글 수정 성공" : "댓글 수정 실패", null));
   }
   
   /**
    * 댓글 삭제
    */

  @PostMapping("/comment/delete.do")
  public ResponseEntity<?> deleteComment(@RequestBody Comment comment,
		  @AuthenticationPrincipal CustomUserDetails userDetails){
	   
	   SecurityUtil.checkAuthorization(userDetails);
	   comment.setUpdateId(userDetails.getUsername());
  	boolean isDeleted = boardService.deleteComment(comment);
  	return ResponseEntity.ok(new ApiResponse<>(isDeleted, isDeleted ? "댓글 삭제 성공" : "댓글 삭제 실패", null));
  }

	
}

    