package br.com.escolaDuartina.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.repository.UserRepository;

@Component
public class AdminUserInitializer implements ApplicationRunner {
	private static final String ADMIN_EMAIL = "admin@gmail.com";

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JdbcTemplate jdbcTemplate;

	public AdminUserInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
			JdbcTemplate jdbcTemplate) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		// Alinha a sequência com os IDs existentes antes de criar o administrador.
		jdbcTemplate.queryForObject(
				"SELECT setval('sq_user', GREATEST(COALESCE((SELECT MAX(user_id) FROM users), 99), 99))",
				Long.class);

		User admin = userRepository.findUserByEmail(ADMIN_EMAIL).orElseGet(User::new);
		admin.setName("admin");
		admin.setEmail(ADMIN_EMAIL);
		admin.setPassword(passwordEncoder.encode("admin"));
		admin.setRoles(new ArrayList<>(List.of("ADMIN", "USER")));
		admin.setApproved(true);
		userRepository.save(admin);
	}
}
