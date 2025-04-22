package back.service.board;

import java.util.List;

import back.exception.HException;
import back.model.board.Board;
import back.model.board.Comment;


public interface BoardService {
	
	public List getBoardList(Board board);
	
    public Board getBoardById(String boardId);
    
    public boolean createBoard(Board board) throws HException;
    
    public boolean updateBoard(Board board);
    
    public boolean deleteBoard(Board board);
    
    public boolean createComment(Comment comment);

    public boolean updateComment(Comment comment);
    
    public boolean deleteComment(Comment comment);

	

}
