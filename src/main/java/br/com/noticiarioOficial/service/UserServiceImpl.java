package br.com.noticiarioOficial.service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.noticiarioOficial.model.User;
import br.com.noticiarioOficial.repository.IUserService;
import br.com.noticiarioOficial.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public Integer saveUser(User user) {
		String passwd = user.getPassword();
		String encodedPasswod = passwordEncoder.encode(passwd);
		user.setPassword(encodedPasswod);
		user = userRepo.save(user);
		return user.getId();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional opt = userRepo.findUserByEmail(email);

		if (opt.isEmpty())
			throw new UsernameNotFoundException("User with email: " + email + " not found !");
		else {
			User user = (User) opt.get();
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
					(Collection<? extends GrantedAuthority>) user.getRoles().stream()
							.map(role -> new SimpleGrantedAuthority((String) role)).collect(Collectors.toSet()));
		}
	}
}