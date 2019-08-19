package online.kabour.springbootbase.securityjwt.security.auth.ajax;

import com.alibaba.fastjson.JSONObject;
import online.kabour.springbootbase.securityjwt.common.ErrorCode;
import online.kabour.springbootbase.securityjwt.common.ResultResponse;
import online.kabour.springbootbase.securityjwt.security.exceptions.AuthMethodNotSupportedException;
import online.kabour.springbootbase.securityjwt.security.exceptions.InvalidJwtTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class AjaxAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException e) throws IOException, ServletException {

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");

		PrintWriter printWriter = response.getWriter();
		if (e instanceof BadCredentialsException
				|| e instanceof UsernameNotFoundException) {
			printWriter.write(JSONObject.toJSONString(ResultResponse.failed("用户名或密码错误", ErrorCode.BAD_CREDENTIALS)));
		} else if (e instanceof AuthMethodNotSupportedException) {
			printWriter.write(JSONObject.toJSONString(ResultResponse.failed("Auth method not supported", ErrorCode.AUTH_METHOD_NOT_SUPPORTED)));
		} else if (e instanceof InvalidJwtTokenException) {
			printWriter.write(JSONObject.toJSONString(ResultResponse.failed(e.getMessage(), ErrorCode.INVALID_JWT_TOKEN)));
		} else {
			printWriter.write(JSONObject.toJSONString(ResultResponse.failed(e.getMessage(), ErrorCode.AUTHENTICATION_FAILED)));
		}
	}

}
