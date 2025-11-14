package RestController.repositories;


import Jersey.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("select i from Item i join fetch i.category c where c.id = :categoryId")
    Page<Item> findByCategoryIdWithJoin(Long categoryId, Pageable pageable);
}