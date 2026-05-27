package oris.travelcommunity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import oris.travelcommunity.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
