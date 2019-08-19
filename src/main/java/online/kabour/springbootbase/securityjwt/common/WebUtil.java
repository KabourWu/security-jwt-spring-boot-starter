package online.kabour.springbootbase.securityjwt.common;

import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class WebUtil {
	private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
	private static final String X_REQUESTED_WITH = "X-Requested-With";

	private static final String CONTENT_TYPE = "Content-type";
	private static final String CONTENT_TYPE_JSON = "application/json";

	public static boolean isAjax(HttpServletRequest request) {
		return XML_HTTP_REQUEST.equals(request.getHeader(X_REQUESTED_WITH));
	}

	public static boolean isAjax(SavedRequest request) {
		return request.getHeaderValues(X_REQUESTED_WITH).contains(XML_HTTP_REQUEST);
	}

	public static boolean isContentTypeJson(SavedRequest request) {
		return request.getHeaderValues(CONTENT_TYPE).contains(CONTENT_TYPE_JSON);
	}


	/**
	 * 时间游标
	 *
	 * @param date  时间点
	 * @param num   时间点移动的数量
	 * @param field 单位 比如：Calendar.HOUR
	 * @return
	 */
	public static Date dateCursor(Date date, int num, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, num);
		return calendar.getTime();
	}

	/**
	 * 将 秒，毫秒 等 转换成 中文 's天s小时s分s秒'显示
	 *
	 * @param duration
	 * @param timeUnit
	 * @return
	 */
	public static String durationTransferToChinese(Long duration, TimeUnit timeUnit) {

		long secondsDuration = TimeUnit.SECONDS.convert(duration, timeUnit);
		long day = secondsDuration / (24 * 3600);
		long hour = secondsDuration % (24 * 3600) / 3600;
		long minute = secondsDuration % 3600 / 60;
		long second = secondsDuration % 60;
		StringBuilder builder = new StringBuilder();
		if (day > 0) {
			builder.append(day).append("天");
		}
		if (day + hour > 0) {
			builder.append(hour).append("小时");
		}
		if (day + hour + minute > 0) {
			builder.append(minute).append("分");
		}
		builder.append(second).append("秒");
		return builder.toString();
	}


}
