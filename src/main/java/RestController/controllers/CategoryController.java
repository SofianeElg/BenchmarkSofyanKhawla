package RestController.controllers;


import Jersey.entities.Category;
import RestController.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/.")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // GET /categories?page=&size=
    @GetMapping
    public Page<Category> getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return categoryRepository.findAll(PageRequest.of(page, size));
    }

    // GET /categories/{id}
    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    // POST /categories
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    // PUT /categories/{id}
    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        Category existing = categoryRepository.findById(id).orElseThrow();
        existing.setCode(category.getCode());
        existing.setName(category.getName());
        existing.setUpdatedAt(category.getUpdatedAt());
        return categoryRepository.save(existing);
    }

    // DELETE /categories/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
    }
}