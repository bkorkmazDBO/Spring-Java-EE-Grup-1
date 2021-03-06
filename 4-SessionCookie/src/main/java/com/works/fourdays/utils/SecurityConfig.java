package com.works.fourdays.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;

import com.works.fourdays.models.Admin;

@Configuration
public class SecurityConfig implements Filter {
	
	final UserControl userControl;
	public SecurityConfig( UserControl userControl ) {
		this.userControl = userControl;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = ( HttpServletResponse ) response;

		
		String url = req.getRequestURI();
		boolean status = pagePermission(url);
		boolean session_status = req.getSession().getAttribute("admin") == null;
		if (!status) {
			
			if (session_status) {
				// Cookie Control
				userControl.cookieControl();
				session_status = req.getSession().getAttribute("admin") == null;
			}
			
			
			if (session_status) {
				try {
					res.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
					res.setHeader("Location", "/");
				} catch (Exception e) {}
			}else
			{
				Admin adm = (Admin) req.getSession().getAttribute("admin");
				req.setAttribute("admin_info", adm);
			}
		}

		
		chain.doFilter(req, res);
	}
	

	
	public boolean pagePermission( String url ) {
		//System.out.println("url : " + url);
		boolean status = false;
		String[] pages = {"/", "/adminLogin"};
		String[] types = {".css", ".js", ".png", ".jpg", ".gif", ".map"};
		
		if ( url.contains("h2-console") ) {
			return true;
		}
		
		for (String item : pages) {
			if (item.equals(url)) {
				status = true;
				break;
			}
		}
		
		for (String item : types) {
			if ( url.endsWith(item) ) {
				status = true;
				break;
			}
		}
		
		return status;
	}
	
	
	
}
