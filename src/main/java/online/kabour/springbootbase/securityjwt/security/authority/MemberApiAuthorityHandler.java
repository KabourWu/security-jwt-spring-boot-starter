package online.kabour.springbootbase.securityjwt.security.authority;

/**
 * 普通会员资源接口处理
 *
 * @author kabour
 * @date 2019/7/12 21:36
 */
public interface MemberApiAuthorityHandler {

	/**
	 * @param authorityName
	 * @param description
	 * @ApiAuthority.Type.MEMBER 类型的回调处理
	 */
	void handle(String authorityName, String description);
}
