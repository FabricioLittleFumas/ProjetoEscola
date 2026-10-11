package br.com.escolaDuartina.security;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.repository.UserRepository;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		@SuppressWarnings("unchecked")
		Optional<User> opt = (Optional<User>) userRepo.findUserByEmail(email);

		if (opt.isEmpty()) {
			throw new UsernameNotFoundException("User with email: " + email + " not found !");
		} else {
			User user = opt.get();
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
					(Collection<? extends GrantedAuthority>) user.getRoles().stream()
							.map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toSet()));
		}
	}
}
