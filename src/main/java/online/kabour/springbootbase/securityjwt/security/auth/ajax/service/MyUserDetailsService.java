package online.kabour.springbootbase.securityjwt.security.auth.ajax.service;


import online.kabour.springbootbase.securityjwt.security.model.MyUserDetails;

import java.util.Date;

/**
 * @author kabour
 * @date 2019/6/8 21:49
 */
public interface MyUserDetailsService {

	MyUserDetails loadMyUserDetailsByUsername(String username);

	/**
	 * 更新用户登录时间
	 */
	void updateMyUserDetailsLoginTimeByUsername(String username);

	/**
	 * @param username
	 * @param count    出错数量
	 * @return
	 */
	void updateMyUserDetailsSerialTrialErrorCountByUsername(String username, Byte count);

	/**
	 * 密码错误次数加一
	 *
	 * @param username
	 * @return
	 */
	Byte myUserDetailsSerialTrialErrorCountPlusOne(String username);

	/**
	 * 更新账户锁定时间
	 *
	 * @param date
	 */
	void updateMyUserDetailsLockedTime(String username, Date date);
}
