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

import cn.halen.data.dao.FenXiaoShangDao;
import cn.halen.data.dao.UserDao;
import cn.halen.data.pojo.FenXiaoShang;

public class FenXiaoShangFilterChain extends GenericFilterBean {

	@Autowired
	private FenXiaoShangDao fenXiaoShangDao;
	@Autowired
	private UserDao userDao;
	
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
		httpSession.setAttribute("username", username);
		
		Object obj = httpSession.getAttribute("CURRENT_FENXIAOSHANG");
		FenXiaoShang fenXiaoShang = null;
		if(null!=obj) {
			fenXiaoShang = (FenXiaoShang) obj;
		} else {
			int userId = userDao.getUserByUsername(username).getId();
			fenXiaoShang = fenXiaoShangDao.getByUserId(userId);
			httpSession.setAttribute("CURRENT_FENXIAOSHANG", fenXiaoShang);
		}
		FenXiaoShangHolder.set(fenXiaoShang);
		chain.doFilter(request, response);
	}
}
