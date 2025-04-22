
package back.util;


import org.springframework.http.HttpStatus;

import back.exception.HException;
import back.model.common.CustomUserDetails;

public class SecurityUtil {

   public static void checkAuthorization(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new HException("로그인 필요", HttpStatus.UNAUTHORIZED);
        }
    }
   
    public static void checkAuthorization(CustomUserDetails userDetails, String userId) {
        if (userDetails == null) {
            throw new HException("로그인 필요", HttpStatus.UNAUTHORIZED);
        }
        if (!userDetails.getUser().getUserId().equals(userId)) {
            throw new HException("권한 없음", HttpStatus.FORBIDDEN);
        }
    }
}
