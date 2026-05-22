package oris.travelcommunity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import oris.travelcommunity.models.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
