package br.com.escolaDuartina.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserInfoUserDetailsService userDetailsService;

	// Lista de rotas que não precisam de autenticação
	private static final String[] PUBLIC_ROUTES = {"/alunos", "/authenticate", "/register", "/saveUser", "/home" };

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		

		String path = request.getRequestURI();
//		System.out.println("cheguei aqui 1");
//		System.out.println(request.getRequestURI());
//		System.out.println("cheguei aqui 1");
		// Verifica se a rota é pública
		if (isPublicRoute(path, request.getMethod())) {
//			System.out.println("cheguei aqui 2");
//			System.out.println(request.getMethod());
			chain.doFilter(request, response);
			return;				
		}
		if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublicRoute(path, request.getMethod())) {
		    chain.doFilter(request, response);
		    return;
		}

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;
//		System.out.println("token bearer");
//		System.out.println(requestTokenHeader);
//		request.getHeaderNames().asIterator().forEachRemaining(item ->{
//			System.out.println(item);
//		});
		// Token JWT está no formato "Bearer token"
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			System.out.println("token");
			System.out.println(requestTokenHeader.substring(7));
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
//				System.out.println("username");
//				System.out.println(username);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			// Se a rota não for pública e não tiver token, retorna erro
			if (requestTokenHeader == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is required");
				return;
			}
			logger.warn("JWT Token does not begin with Bearer String");
		}

		// Validar token
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

	private boolean isPublicRoute(String path, String method) {
		for (String route : PUBLIC_ROUTES) {
			System.out.println(route);
			if (path.contains(route)) {
//				if (route.equals("/alunos") && method.equals("POST")) return false;
				return true;
			}
		}
//		System.out.println("path final");
//		System.out.println(path);
		return false;
	}
}