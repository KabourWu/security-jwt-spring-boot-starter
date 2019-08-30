package online.kabour.springbootbase.securityjwt.security.auth.ajax.service;


import online.kabour.springbootbase.securityjwt.security.model.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;

/**
 * @author kabour
 * @date 2019/6/8 21:49
 */
public interface MyUserDetailsService extends UserDetailsService {


    MyUserDetails loadUserByUsername(String username);

    /**
     * 更新用户登录时间
     */
    void updateMyUserDetailsLoginTimeByUsername(String username);

    /**
     * 复位用户的连续出错次数
     *
     * @param username
     */
    void resetMyUserDetailsSerialTrialErrorCount(String username);

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
