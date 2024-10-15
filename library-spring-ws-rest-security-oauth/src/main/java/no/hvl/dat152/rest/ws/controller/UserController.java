/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	// TODO authority annotation
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(){

        List<User> users = userService.findAllUsers();

        if(users.isEmpty())

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id) throws UserNotFoundException, OrderNotFoundException{

        try {
            User user = userService.findUser(id);
            //addLinks(user.getOrders());
        // Add a self-link for the user
        Link selfLink = linkTo(methodOn(UserController.class).getUser(id)).withSelfRel();
        user.add(selfLink);

        // Add HATEOAS links to each order for this user
        for (Order order : user.getOrders()) {
            Link orderLink = linkTo(methodOn(UserController.class).getUserOrder(id, order.getId())).withRel("order");
            order.add(orderLink);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    // TODO - createUser (@Mappings, URI=/users, and method)
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // TODO - updateUser (@Mappings, URI, and method)
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) throws UserNotFoundException {
        User updatedUser = userService.updateUser(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // TODO - deleteUser (@Mappings, URI, and method)
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws UserNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO - getUserOrders (@Mappings, URI=/users/{id}/orders, and method)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/users/{id}/orders")
    public ResponseEntity<Set<Order>> getUserOrders(@PathVariable("id") Long id) throws UserNotFoundException {
        Set<Order> orders = userService.getUserOrders(id);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<Order> getUserOrder(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId) throws UserNotFoundException, OrderNotFoundException {
        Order order = userService.getUserOrder(userId, orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // TODO - deleteUserOrder (@Mappings, URI, and method)
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<Void> deleteUserOrder(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId) throws UserNotFoundException, OrderNotFoundException {
        userService.deleteOrderForUser(userId, orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO - createUserOrder (@Mappings, URI, and method) + HATEOAS links
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<User> createUserOrder(@PathVariable("userId") Long userId, @RequestBody Order order) throws UserNotFoundException, OrderNotFoundException {
        userService.createOrderForUser(userId, order);
        User user = userService.findUser(userId);

        // Add HATEOAS links as before
        Link selfLink = linkTo(methodOn(UserController.class).getUser(userId)).withSelfRel();
        user.add(selfLink);

        for (Order orders : user.getOrders()) {
            Link orderLink = linkTo(methodOn(UserController.class).getUserOrder(userId, orders.getId())).withSelfRel();
            orders.add(orderLink);

            Link deleteLink = linkTo(methodOn(UserController.class).deleteUserOrder(userId, orders.getId())).withRel("deleteOrder");
            orders.add(deleteLink);
        }

        Link userOrdersLink = linkTo(methodOn(UserController.class).getUserOrders(userId)).withRel("allOrders");
        user.add(userOrdersLink);

        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUserOrders(userId)).toUri())
                .body(user);
    }
}
