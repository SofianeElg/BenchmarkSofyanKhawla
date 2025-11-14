package Jersey.resources;

import Jersey.dao.ItemDAO;
import Jersey.entities.Item;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


import java.util.List;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    @Inject
    private ItemDAO itemDAO;

    @GET
    public List<Item> getAll() {
        return itemDAO.findAll();
    }

    @GET
    @Path("/{id}")
    public Item getById(@PathParam("id") Long id) {
        return itemDAO.findById(id);
    }

    @GET
    @Path("/byCategory/{categoryId}")
    public List<Item> getByCategory(@PathParam("categoryId") Long categoryId) {
        return itemDAO.findByCategory(categoryId);
    }

    @POST
    public Item create(Item item) {
        return itemDAO.save(item);
    }

    @PUT
    @Path("/{id}")
    public Item update(@PathParam("id") Long id, Item item) {
        item.setId(id);
        return itemDAO.save(item);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        itemDAO.delete(id);
    }
}