package space.damirka.socialworldbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import space.damirka.socialworldbackend.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
