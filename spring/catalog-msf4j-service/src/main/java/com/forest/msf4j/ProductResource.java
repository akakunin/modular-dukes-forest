package com.forest.msf4j;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.forest.model.Product;
import com.forest.usecase.catalog.ProductManager;

@Component
@Path("/product")
public class ProductResource {
	@Autowired 
	private ProductManager productManager;

	@GET
	@Produces("application/json")
	public List<Product> getAll () {
		return productManager.getAll();
	}

	@GET
	@Produces("application/json")
	@Path("{id}")
	public Product get (@PathParam ("id") int id) {
		return productManager.getProduct(id);
	}
	
	@POST
	@Consumes("application/json")
	void addProduct(Product product) {
		productManager.createProduct(product);
	}
}
