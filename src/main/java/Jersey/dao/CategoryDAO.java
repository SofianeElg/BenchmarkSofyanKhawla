package Jersey.dao;


import Jersey.entities.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;


import java.util.List;

@Transactional
public class CategoryDAO {
    @PersistenceContext
    private EntityManager em;

    public List<Category> findAll() {
        return em.createQuery("from Category", Category.class).getResultList();
    }

    public Category findById(Long id) {
        return em.find(Category.class, id);
    }

    public Category save(Category category) {
        if (category.getId() == null)
            em.persist(category);
        else
            category = em.merge(category);
        return category;
    }

    public void delete(Long id) {
        Category c = findById(id);
        if (c != null) em.remove(c);
    }
}