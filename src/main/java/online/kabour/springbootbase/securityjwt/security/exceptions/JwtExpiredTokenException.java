package online.kabour.springbootbase.securityjwt.security.exceptions;

import online.kabour.springbootbase.securityjwt.security.model.token.JwtToken;
import org.springframework.security.core.AuthenticationException;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class JwtExpiredTokenException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;
    
    private JwtToken token;

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
