package viet.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import viet.app.exception.InvalidDataException;
import viet.app.model.RedisToken;
import viet.app.repository.RedisTokenRepository;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    public void save(RedisToken token) {
        redisTokenRepository.save(token);
    }

    public void remove(String id) {
        isExists(id);
        redisTokenRepository.deleteById(id);
    }

    public boolean isExists(String id) {
        if (!redisTokenRepository.existsById(id)) {
            throw new InvalidDataException("Token not exists");
        }
        return true;
    }
}
