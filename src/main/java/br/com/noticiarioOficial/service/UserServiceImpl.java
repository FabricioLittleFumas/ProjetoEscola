package br.com.noticiarioOficial.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.noticiarioOficial.model.User;
import br.com.noticiarioOficial.repository.IUserService;
import br.com.noticiarioOficial.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

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
}