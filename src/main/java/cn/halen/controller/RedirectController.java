package cn.halen.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectController {
	
	@RequestMapping(value="/login")
	public String login(Model model) {
		return "login";
	}
	
	@RequestMapping(value="/access_denied")
	public String accessDenied() {
		return "access_denied";
	}
	
	@RequestMapping(value="/index")
	public String index(Model model, HttpServletRequest req) {
//		Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String name = "";
//		if(object instanceof String) {
//			name = (String) object;
//		} else {
//			User user = (User)object;
//			name = user.getUsername();
//		}
//		req.getSession().setAttribute("username", name);
		
		return "index";
	}
	
	@RequestMapping(value="/login_failed")
	public String loginFailed(Model model, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		model.addAttribute("error", "true");
		return "login";
	}
}
