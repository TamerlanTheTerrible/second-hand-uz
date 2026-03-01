package me.timur.secondhanduz.user.application.port.out;

import me.timur.secondhanduz.user.domain.User;

import java.util.Optional;

/**
 * Output port: persistence contract for {@link User} entities.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
