/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	// TODO authority annotation
    @Autowired
    private OrderService orderService;

    // TODO - getAllBorrowOrders (@Mappings, URI=/orders, and method) + filter by expiry and paginate
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<Object> getAllBorrowOrders(@RequestParam(value = "expiry", required = false)String expiryDate, org.springframework.data.domain.Pageable pageable){
        List<Order> orders;
        // If expiry date is provided, filter orders by expiry date
        if (expiryDate != null) {
            try {
                LocalDate expiry = LocalDate.parse(expiryDate);
                orders = orderService.findByExpiryDate(expiry, pageable);
            } catch (OrderNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Otherwise, fetch all orders with pagination
            orders = orderService.findAllOrders();
        }

        // If no orders are found, return 204 No Content
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // TODO - getBorrowOrder (@Mappings, URI=/orders/{id}, and method)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> getBorrowOrder(@PathVariable("id") Long id) throws OrderNotFoundException{

        Order order = orderService.findOrder(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // TODO - updateOrder (@Mappings, URI=/orders/{id}, and method)
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ADMIN')")
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@RequestBody Order order,@PathVariable ("id") long id) {
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try {
            Order neworder = orderService.updateOrder(order , id);
            return new ResponseEntity<>(neworder, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // TODO - deleteBookOrder (@Mappings, URI=/orders/{id}, and method)
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ADMIN')")
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Order> deleteBookOrder(@PathVariable("id") Long id) {
        try {
            orderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
