package cn.halen.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.GenericFilterBean;

import cn.halen.data.mapper.AdminMapper;

public class UserFilterChain extends GenericFilterBean {

	@Autowired
	private AdminMapper adminMapper;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpSession httpSession = req.getSession();
		
		String springSecurityContextKey = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
		Object contextFromSession = httpSession.getAttribute(springSecurityContextKey);

        if (contextFromSession == null || !(contextFromSession instanceof SecurityContext)) {
        	chain.doFilter(request, response);
        	return;
        }

        SecurityContext securityContext = (SecurityContext) contextFromSession;
		Object object = securityContext.getAuthentication().getPrincipal();
		User user = (User)object;
		String username = user.getUsername();
		
		Object obj = httpSession.getAttribute("CURRENT_USER");
		cn.halen.data.pojo.User user1 = null;
		if(null != obj) {
			user1 = (cn.halen.data.pojo.User) obj;
		} else {
			user1 = adminMapper.selectUser(username);
			httpSession.setAttribute("CURRENT_USER", user1);
		}
		UserHolder.set(user1);
		
		chain.doFilter(request, response);
	}
}
