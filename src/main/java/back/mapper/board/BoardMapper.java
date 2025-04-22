package back.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import back.model.board.Board;
import back.model.board.Comment;

@Mapper
public interface BoardMapper {

    public List<Board> getBoardList(Board board);

    public int getTotalBoardCount(Board board);

    public Board getBoardById(String boardId);

    public int create(Board board);

    public int update(Board board);

    public int delete(Board board);

    public List<Comment> getCommentsByBoardId(String boardId);

    public int insertComment(Comment comment);

    public int updateComment(Comment comment);

    public int deleteComment(Comment comment);
}