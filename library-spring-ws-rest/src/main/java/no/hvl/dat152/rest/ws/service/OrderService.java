/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.time.LocalDate;
import java.util.List;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UpdateOrderFailedException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;

/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	public Order saveOrder(Order order) {
		
		order = orderRepository.save(order);
		
		return order;
	}
	
	public Order findOrder(Long id) throws OrderNotFoundException {
		
		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));
		
		return order;
	}
	
	public void deleteOrder(Long id) throws OrderNotFoundException{
		try {
			orderRepository.deleteById(id);
		}catch(Exception e) {
			throw new OrderNotFoundException("Order with id = "+id+" not found!");
		}
	}
	
	public List<Order> findAllOrders(){
		return (List<Order>) orderRepository.findAll();
		
	}
	
	public List<Order> findByExpiryDate(LocalDate expiry, Pageable page) throws OrderNotFoundException {
		List<Order> order = orderRepository.findByExpiryBefore(expiry, page).getContent();
		return order;
	}
	
	public Order updateOrder(Order order, long id) throws UpdateOrderFailedException{
		Order order1 = new Order();
		try {
			order1 = findOrder(id);
		} catch (OrderNotFoundException e) {
			throw new RuntimeException(e);
		}
		order1.setIsbn(order.getIsbn());
		order1.setExpiry(order.getExpiry());
		return order1;
	}

}
