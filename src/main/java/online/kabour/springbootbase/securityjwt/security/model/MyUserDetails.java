package online.kabour.springbootbase.securityjwt.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class MyUserDetails extends User {
	//连续输入出错的次数
	private Byte serialTrialCount;
	//账号锁定的时间
	private Date lockedTime;
	private UserContext.UserBasicDetail userBasicDetail;

	public MyUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
	                     boolean credentialsNonExpired, boolean accountNonLocked,
	                     byte serialTrialCount, Date lockedTime, UserContext.UserBasicDetail userBasicDetail,
	                     Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.serialTrialCount = serialTrialCount;
		this.lockedTime = lockedTime;
		this.userBasicDetail = userBasicDetail;
	}

	public UserContext.UserBasicDetail getUserBasicDetail() {
		return userBasicDetail;
	}

	public Date getLockedTime() {
		return lockedTime;
	}

	public Byte getSerialTrialCount() {
		return serialTrialCount;
	}
}
