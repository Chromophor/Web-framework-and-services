/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import no.hvl.dat152.rest.ws.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

	// TODO copy your solutions from previous tasks!
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;


    public List<User> findAllUsers(){

        List<User> allUsers = (List<User>) userRepository.findAll();

        return allUsers;
    }

    public User findUser(Long userid) throws UserNotFoundException {

        User user = userRepository.findById(userid)
                .orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));

        return user;
    }


    // TODO public User saveUser(User user)
    public User saveUser(User user) {
        userRepository.save(user);
        return user;
    }

    // TODO public void deleteUser(Long id) throws UserNotFoundException
    public void deleteUser(Long userid) throws UserNotFoundException {
        try{
            userRepository.deleteById(userid);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("User with id: "+userid+" not found");
        }
    }

    // TODO public User updateUser(User user, Long id)
    public User updateUser(Long userid, User user) throws UserNotFoundException {
        try{
            User changeduser = findUser(userid);
            changeduser = user;
            return changeduser;
        }catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("User with id: "+userid+" not found");
        }
    }

    // TODO public Set<Order> getUserOrders(Long userid)
    public Set<Order> getUserOrders(Long userid) throws UserNotFoundException {
        try {
            User user = findUser(userid);
            return user.getOrders();
        } catch (UserNotFoundException e){
            throw new UserNotFoundException("User with id: "+userid+" not found");
        }
    }
    // TODO public Order getUserOrder(Long userid, Long oid)
    public Order getUserOrder(Long userid, Long orderid) throws OrderNotFoundException, UserNotFoundException {
        Set<Order> orders = getUserOrders(userid);
        for (Order order : orders) {
            if (order.getId().equals(orderid)) {
                return order;
            }
        }
        throw new OrderNotFoundException("Order with id: "+orderid+" not found");
    }

    // TODO public void deleteOrderForUser(Long userid, Long oid)
    public void deleteOrderForUser(Long userid, Long orderid) {
        try{
            User user = findUser(userid);
            user.removeOrder(getUserOrder(userid, orderid));
        } catch (UserNotFoundException | OrderNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    // TODO public User createOrdersForUser(Long userid, Order order)
    public void createOrderForUser(Long userid, Order order) throws UserNotFoundException {
        try{
            User user = findUser(userid);
            user.addOrder(order);
            userRepository.save(user);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
