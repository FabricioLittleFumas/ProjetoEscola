package br.com.escolaDuartina.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.escolaDuartina.model.JwtRequest;
import br.com.escolaDuartina.model.JwtResponse;
import br.com.escolaDuartina.model.RefreshToken;
import br.com.escolaDuartina.security.JwtTokenUtil;
import br.com.escolaDuartina.security.UserInfoUserDetailsService;
import br.com.escolaDuartina.service.RefreshTokenService;

@RestController
@RequestMapping("/autentica")
@CrossOrigin
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserInfoUserDetailsService userDetailsService;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

		// Gera os DOIS tokens
		final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
		final String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

		return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
		String refreshTokenValue = request.get("refreshToken");

		if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("message", "Refresh token ausente"));
		}

		try {
			RefreshToken stored = refreshTokenService.findByToken(refreshTokenValue)
					.orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));

			if (refreshTokenService.isExpired(stored)) {
				refreshTokenService.deleteByToken(refreshTokenValue);
				return ResponseEntity.status(401).body(Map.of("message", "Refresh token expirado"));
			}

			String email = stored.getUser().getEmail();
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);

			String novoAccessToken = jwtTokenUtil.generateAccessToken(userDetails);

			// Rotação: invalida o antigo e cria um novo
			refreshTokenService.deleteByToken(refreshTokenValue);
			String novoRefreshToken = refreshTokenService.createRefreshToken(email);

			return ResponseEntity.ok(Map.of("accessToken", novoAccessToken, "refreshToken", novoRefreshToken));
		} catch (Exception e) {
			return ResponseEntity.status(401).body(Map.of("message", "Falha ao renovar token"));
		}
	}

	private void authenticate(String email, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}