package br.com.noticiarioOficial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Autowired
	private UserDetailsService uds;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Criando o matcher para logout usando PathPatternRequestMatcher
		PathPatternRequestMatcher logoutMatcher = PathPatternRequestMatcher.withDefaults().matcher("/logout");

		http.authorizeHttpRequests(auth -> auth
				// Rotas públicas (permitidas sem autenticação)
				.requestMatchers("/home", "/usuarios", "/saveUser").permitAll()

				// Rotas que exigem autenticação
				.requestMatchers("/welcome").authenticated()

				// Rotas com autoridades específicas
				.requestMatchers("/admin").hasAuthority("Admin").requestMatchers("/mgr").hasAuthority("Manager")
				.requestMatchers("/emp").hasAuthority("Employee").requestMatchers("/hr").hasAuthority("HR")
				.requestMatchers("/common").hasAnyAuthority("Employee", "Manager", "Admin")

				// Qualquer outra requisição exige autenticação
				.anyRequest().authenticated()).formLogin(form -> form.defaultSuccessUrl("/welcome", true).permitAll())
				.logout(logout -> logout.logoutRequestMatcher(logoutMatcher) // Usando PathPatternRequestMatcher
						.permitAll())
				.exceptionHandling(exception -> exception.accessDeniedPage("/accessDenied"))
				.authenticationProvider(authenticationProvider());

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(uds);
		authenticationProvider.setPasswordEncoder(encoder);
		return authenticationProvider;
	}
}