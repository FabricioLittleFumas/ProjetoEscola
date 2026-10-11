package br.com.escolaDuartina.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.escolaDuartina.excecao.Excecao;
import br.com.escolaDuartina.model.RefreshToken;
import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.repository.RefreshTokenRepository;
import br.com.escolaDuartina.repository.UserRepository;

@Service
public class RefreshTokenService {

    // 7 dias em milissegundos
    private static final long REFRESH_TOKEN_DURATION_MS = 7L * 24 * 60 * 60 * 1000;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String createRefreshToken(String email) {
        User user = (User) userRepository.findUserByEmail(email)
                .orElseThrow(() -> new Excecao("Usuário não encontrado: " + email));

        // Remove tokens antigos do usuário (evita acúmulo)
        refreshTokenRepository.deleteByUser(user);

        // Gera um token opaco (UUID) — mais seguro que JWT para refresh
        String tokenValue = UUID.randomUUID().toString() + "-" + UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken(
                tokenValue,
                user,
                new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION_MS)
        );

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().before(new Date());
    }
}