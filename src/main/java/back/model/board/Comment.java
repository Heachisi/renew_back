package back.model.board;

import back.model.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 댓글(Comment) 모델 클래스
 */
@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class Comment extends Model {
    private int commentId; // 댓글 고유 ID
    private int boardId; // 게시판 ID
    private Integer parentCommentId; // 부모 댓글 ID (대댓글)
    private String content; // 댓글 내용
    private String delYn; // 삭제 여부 (Y/N)
}