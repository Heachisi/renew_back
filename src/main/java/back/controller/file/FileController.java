package back.controller.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import back.model.common.PostFile;
import back.service.file.FileService;
import back.util.ApiResponse;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 게시판 관련 파일 업로드 및 다운로드 기능을 처리하는 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileController {

	/** 
	 * 업로드된 이미지의 다운로드 URL에 사용될 베이스 경로 
	 * (application.properties의 myapp.apiBaseUrl 값 사용) 
	 */
	@Value("${myapp.apiBaseUrl}")
	private String apiBaseUrl;

	/** 파일 처리 서비스 */
	@Autowired
	private FileService fileService;

	/** MIME 타입 판별을 위한 서블릿 컨텍스트 */
	@Autowired
	private ServletContext servletContext;

	/**
	 * 일반 파일 다운로드를 처리하는 메서드
	 *
	 * @param fileId 다운로드할 파일의 식별자
	 * @param response 클라이언트로 파일을 전송할 HTTP 응답 객체
	 */
	@GetMapping("/down.do")
	public void downloadFile(@RequestParam("fileId") String fileId, HttpServletResponse response) {
		try {
			// 파일 식별자 기반으로 파일 객체 생성
			PostFile file = new PostFile();
			file.setFileId(Integer.parseInt(fileId));

			// DB 또는 저장소에서 파일 정보 조회
			PostFile selectFile = fileService.getFileByFileId(file);

			if (selectFile == null) {
				// 파일 정보가 없는 경우
				response.getWriter().write("파일을 찾을 수 없습니다.");
				return;
			}

			// 실제 파일 객체 생성
			File downloadFile = new File(selectFile.getFilePath());

			if (!downloadFile.exists()) {
				// 파일이 존재하지 않을 경우
				response.getWriter().write("파일이 존재하지 않습니다.");
				return;
			}

			// 다운로드 응답 설정
			String fileName = selectFile.getFileName();
			response.setContentType("application/octet-stream"); // 일반 바이너리 파일
			response.setContentLength((int) downloadFile.length());
			response.setHeader("Content-Disposition", 
				"attachment; filename=" + URLEncoder.encode(fileName, "UTF-8")); // 첨부파일로 다운로드 처리
		} catch (Exception e) {
			e.printStackTrace(); // 예외 로깅
		}
	}

	/**
	 * 이미지 파일 다운로드 처리 (브라우저에 바로 띄우기용)
	 *
	 * @param fileId 다운로드할 이미지 파일의 식별자
	 * @param response 클라이언트로 이미지 파일을 전송할 HTTP 응답 객체
	 */
	@GetMapping("/imgDown.do")
	public void downloadImage(@RequestParam("fileId") String fileId, HttpServletResponse response) {
		try {
			PostFile file = new PostFile();
			file.setFileId(Integer.parseInt(fileId));
			PostFile selectFile = fileService.getFileByFileId(file);

			if (selectFile == null) {
				response.getWriter().write("파일을 찾을 수 없습니다.");
				return;
			}

			File downloadFile = new File(selectFile.getFilePath());
			if (!downloadFile.exists()) {
				response.getWriter().write("파일이 존재하지 않습니다.");
				return;
			}

			// 파일 MIME 타입 설정 (예: image/jpeg 등)
			String mimeType = servletContext.getMimeType(selectFile.getFilePath());
			if (mimeType == null)
				mimeType = "application/octet-stream";

			response.setContentType(mimeType); // 이미지라면 image/png 등
			response.setContentLength((int) downloadFile.length());
			response.setHeader("Content-Disposition",
					"inline; filename=" + URLEncoder.encode(selectFile.getFileName(), "UTF-8")); // 브라우저에 표시

			// 파일 스트림을 클라이언트에 전송
			try (FileInputStream fis = new FileInputStream(downloadFile);
				 OutputStream out = response.getOutputStream()) {

				byte[] buffer = new byte[4096];
				int bytesRead;

				while ((bytesRead = fis.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // 예외 로깅
		}
	}

	/**
	 * 이미지 파일 업로드 처리
	 *
	 * @param postFile 업로드된 파일 정보가 담긴 PostFile 객체
	 * @param files 업로드된 MultipartFile 리스트
	 * @return 업로드 결과를 담은 JSON 응답
	 */
	@PostMapping(value = "/imgUpload.do", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadImage(
			@ModelAttribute PostFile postFile,
			@RequestPart(value = "files", required = false) List<MultipartFile> files) {

		log.info("이미지 파일 업로드 요청");

		HashMap<String, Object> responseMap = new HashMap<>();
		postFile.setFiles(files);
        boolean isUploadFile = false;
		try {
			// 업로드 경로, 작성자 등 설정
			postFile.setBasePath("img");
			postFile.setCreateId("SYSTEM");

			// 파일 서비스 호출하여 저장
			HashMap resultMap = (HashMap) fileService.insertBoardFiles(postFile);
			isUploadFile = (boolean) resultMap.get("result");

			if (isUploadFile) {
				// 업로드 성공 시 접근 가능한 이미지 URL 추가
				responseMap.put("url", apiBaseUrl + "/api/file/imgDown.do?fileId=" + resultMap.get("fileId"));
			}
		} catch (Exception e) {
			log.error("이미지 파일 업로드 중 오류", e);
			
		}

		return ResponseEntity.ok(new ApiResponse<>(isUploadFile, 
                isUploadFile ? "이미지 파일 업로드 성공" : "이미지 파일 업로드 실패", responseMap));
	}
}
