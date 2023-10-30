package com.eastcom.potevio.rail.securityjwt.security;

import com.alibaba.fastjson.JSONObject;
import com.eastcom.potevio.rail.securityjwt.common.ErrorCode;
import com.eastcom.potevio.rail.securityjwt.common.ResultResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author kabour
 * @date 2019/6/23 15:42
 */
@Component
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		PrintWriter printWriter = response.getWriter();
		printWriter.write(JSONObject.toJSONString(ResultResponse.failed("无权访问", ErrorCode.ACCESS_DENIED)));
		printWriter.flush();
	}

	@ExceptionHandler(AccessDeniedException.class)
	void exception(AccessDeniedException exception, HttpServletResponse response) throws IOException, ServletException {
		handle(null, response, exception);
	}
}
