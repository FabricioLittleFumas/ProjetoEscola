package br.com.escolaDuartina.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.escolaDuartina.security.LogSystemInterceptor;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Configuration
public class LogSystemWebConfig implements WebMvcConfigurer {
	private final LogSystemInterceptor logInterceptor;

	public LogSystemWebConfig(LogSystemInterceptor logInterceptor) {
		this.logInterceptor = logInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logInterceptor);
	}

	@Bean
	Filter contentCachingResponseFilter() {
		return new org.springframework.web.filter.OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
					jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain)
					throws java.io.IOException, jakarta.servlet.ServletException {
				ContentCachingResponseWrapper wrapped = new ContentCachingResponseWrapper(response);
				try {
					filterChain.doFilter(request, wrapped);
				} finally {
					wrapped.copyBodyToResponse();
				}
			}
		};
	}
}
