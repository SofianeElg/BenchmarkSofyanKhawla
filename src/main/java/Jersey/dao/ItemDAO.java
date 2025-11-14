package Jersey.dao;

import Jersey.entities.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;


import java.util.List;

@Transactional
public class ItemDAO {
    @PersistenceContext
    private EntityManager em;

    public List<Item> findAll() {
        return em.createQuery("from Item", Item.class).getResultList();
    }

    public Item findById(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findByCategory(Long categoryId) {
        return em.createQuery("from Item i where i.category.id = :id", Item.class)
                .setParameter("id", categoryId)
                .getResultList();
    }

    public Item save(Item item) {
        if (item.getId() == null)
            em.persist(item);
        else
            item = em.merge(item);
        return item;
    }

    public void delete(Long id) {
        Item i = findById(id);
        if (i != null) em.remove(i);
    }
}