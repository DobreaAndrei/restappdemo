package andrei.demo.users.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import andrei.demo.users.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	Optional<User> findByName(String name);
}