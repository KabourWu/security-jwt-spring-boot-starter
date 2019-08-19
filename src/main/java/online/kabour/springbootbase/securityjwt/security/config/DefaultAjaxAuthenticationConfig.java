package online.kabour.springbootbase.securityjwt.security.config;

import online.kabour.springbootbase.securityjwt.security.CaptchaVerify;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.service.MyAdminDetailsService;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.service.MyMemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author kabour
 * @date 2019/6/9 12:27
 */
public class DefaultAjaxAuthenticationConfig implements AjaxAuthenticationConfig {

    private CaptchaVerify captchaVerify;

    //密码连续不对的次数
    private Byte serialTrialCount = 5;

    //密码连续不对 账号锁定的时长，单位 小时
    private Byte lockedDuration = 5;

    @Autowired(required = false)
    private MyMemberDetailsService myMemberDetailsService;

    @Autowired(required = false)
    private MyAdminDetailsService myAdminDetailsService;

    @Override
    public CaptchaVerify getCaptchaVerify() {
        return captchaVerify;
    }

    public void setCaptchaVerify(CaptchaVerify captchaVerify) {
        this.captchaVerify = captchaVerify;
    }

    @Override
    public Byte getSerialTrialCount() {
        return serialTrialCount;
    }

    public void setSerialTrialCount(Byte serialTrialCount) {
        this.serialTrialCount = serialTrialCount;
    }

    @Override
    public Byte getLockedDuration() {
        return lockedDuration;
    }

    public void setLockedDuration(Byte lockedDuration) {
        this.lockedDuration = lockedDuration;
    }

    @Override
    public MyMemberDetailsService getMemberDetailsService() {
        return myMemberDetailsService;
    }

    @Override
    public MyAdminDetailsService getAdminDetailsService() {
        return myAdminDetailsService;
    }

}
