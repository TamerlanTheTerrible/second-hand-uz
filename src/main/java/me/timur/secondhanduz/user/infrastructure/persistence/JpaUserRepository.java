package me.timur.secondhanduz.user.infrastructure.persistence;

import me.timur.secondhanduz.user.application.port.out.UserRepository;
import me.timur.secondhanduz.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA adapter fulfilling the {@link UserRepository} output port.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

    @Override
    Optional<User> findByEmail(String email);

    @Override
    boolean existsByEmail(String email);
}
