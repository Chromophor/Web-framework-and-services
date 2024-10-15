/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;

import java.time.LocalDate;

import no.hvl.dat152.rest.ws.exceptions.UpdateOrderFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import no.hvl.dat152.rest.ws.security.UserDetailsImpl;
import no.hvl.dat152.rest.ws.security.AuthTokenFilter;
/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	// TODO copy your solutions from previous tasks!

	public Order saveOrder(Order order) {

		order = orderRepository.save(order);

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

	public Order updateOrder(Order order, long id) throws UpdateOrderFailedException {
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

	public Order findOrder(Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {
		
		verifyPrincipalOfOrder(id);
		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));
		
		return order;
	}
	
	private boolean verifyPrincipalOfOrder(Long id) throws UnauthorizedOrderActionException {
		
		JwtAuthenticationToken oauthJwtToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) oauthJwtToken.getDetails();
		// verify if the user sending request is an ADMIN or SUPER_ADMIN
		for(GrantedAuthority authority : userPrincipal.getAuthorities()){
			if(authority.getAuthority().equals("ADMIN")) {
				return true;
			}
		}
		
		// otherwise, make sure that the user is the one who initially made the order
		String email = orderRepository.findEmailByOrderId(id);
		
		if(email.equals(userPrincipal.getEmail()))
			return true;
		
		throw new UnauthorizedOrderActionException("Unauthorized order action!");

	}
}
