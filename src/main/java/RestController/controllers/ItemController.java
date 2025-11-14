package RestController.controllers;



import Jersey.entities.Item;
import RestController.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    // GET /items?page=&size=
    @GetMapping
    public Page<Item> getAll(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return itemRepository.findAll(PageRequest.of(page, size));
    }

    // GET /items/{id}
    @GetMapping("/{id}")
    public Item getById(@PathVariable Long id) {
        return itemRepository.findById(id).orElseThrow();
    }

    // GET /items?categoryId=...
    @GetMapping(params = "categoryId")
    public Page<Item> getByCategory(@RequestParam Long categoryId,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return itemRepository.findByCategoryId(categoryId, PageRequest.of(page, size));
    }

    // POST /items
    @PostMapping
    public Item create(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    // PUT /items/{id}
    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody Item item) {
        Item existing = itemRepository.findById(id).orElseThrow();
        existing.setName(item.getName());
        existing.setPrice(item.getPrice());
        existing.setStock(item.getStock());
        existing.setCategory(item.getCategory());
        existing.setUpdatedAt(item.getUpdatedAt());
        return itemRepository.save(existing);
    }

    // DELETE /items/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemRepository.deleteById(id);
    }
}