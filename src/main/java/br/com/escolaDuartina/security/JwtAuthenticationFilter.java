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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		

//		String path = request.getRequestURI();
		
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			final String requestTokenHeader = request.getHeader("Authorization");
			System.out.println("aqui token 2");
			System.out.println(requestTokenHeader);
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
		System.out.println("aqui token");
		System.out.println(requestTokenHeader);
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			System.out.println("token");
			System.out.println(requestTokenHeader.substring(7));
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
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
//				System.out.println("username");
//				System.out.println(username);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		}

	
		chain.doFilter(request, response);
	}

}