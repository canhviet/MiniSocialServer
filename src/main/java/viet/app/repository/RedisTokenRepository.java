package viet.app.repository;

import org.springframework.data.repository.CrudRepository;
import viet.app.model.RedisToken;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
