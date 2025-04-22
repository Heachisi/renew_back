package back.model.board;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import back.model.Model;
import back.model.common.PostFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드로 매개변수를 하는 생성자 자동 생성
@EqualsAndHashCode(callSuper = true)
public class Board extends Model {

    // 검색 필터
    private String searchText;
    private String startDate;
    private String endDate;

    // 게시글 정보
    private String boardId;
    private String title;
    private String content;
    private String viewCount;

    // 페이징
    private int rn;
    private int startRow;
    private int endRow;
    private int page = 1; // 기본 페이지 1
    private int size = 15; // 기본 페이지 크기 10
    private int totalCount;
    private int totalPages;

    // 연관 데이터
    private List<PostFile> postFiles;
    private List<Comment> comments;

    private List<MultipartFile> files;
    private String remainingFileIds;
}
		