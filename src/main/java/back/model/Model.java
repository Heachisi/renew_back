package back.model;

import lombok.Data;

@Data
public class Model {
    private String createId;   // 생성자 ID
    private String updateId;   // 수정자 ID
    private String createDt;   // 생성일
    private String updateDt;   // 수정일
}
