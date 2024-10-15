/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Role;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.RoleRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class AdminUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public User saveUser(User user) {
		
		user = userRepository.save(user);
		
		return user;
	}
	
	// TODO public User deleteUserRole(Long id, String role)
	// Revoke a role from a user
	public User deleteUserRole(Long id, String roleName) throws UserNotFoundException {// Find the user by ID
		User user = findUser(id);

		// Find the role in the user's list of roles and remove it
		Role roleEntity = roleRepository.findByName(roleName);
		if (roleEntity == null || !user.getRoles().contains(roleEntity)) {
			throw new IllegalArgumentException("Role " + roleName + " not found or user does not have this role.");
		}

		// Remove the role from the user
		user.getRoles().remove(roleEntity);
		return userRepository.save(user);
	}
	// TODO public User updateUserRole(Long id, String role)
	// Assign a role to a user
	public User updateUserRole(Long id, String roleName) throws UserNotFoundException {
		// Find the user by ID
		User user = findUser(id);

		// Find the role in the role repository
		Role roleEntity = roleRepository.findByName(roleName);
		if (roleEntity == null) {
			throw new IllegalArgumentException("Role " + roleName + " not found.");
		}

		// Assign the role to the user
		user.getRoles().add(roleEntity);
		return userRepository.save(user);
	}

	public User findUser(Long id) throws UserNotFoundException {
		
		User user = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+id+" not found"));
		
		return user;
	}
}
