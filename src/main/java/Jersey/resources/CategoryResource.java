package Jersey.resources;

import Jersey.dao.CategoryDAO;
import Jersey.entities.Category;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;



import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    private CategoryDAO categoryDAO;

    @GET
    public List<Category> getAll() {
        return categoryDAO.findAll();
    }

    @GET
    @Path("/{id}")
    public Category getById(@PathParam("id") Long id) {
        return categoryDAO.findById(id);
    }

    @POST
    public Category create(Category category) {
        return categoryDAO.save(category);
    }

    @PUT
    @Path("/{id}")
    public Category update(@PathParam("id") Long id, Category category) {
        category.setId(id);
        return categoryDAO.save(category);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        categoryDAO.delete(id);
    }
}