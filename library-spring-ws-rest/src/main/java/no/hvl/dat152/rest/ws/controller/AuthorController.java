/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

	
	// TODO - getAllAuthor (@Mappings, URI, and method)
	
	// TODO - getAuthor (@Mappings, URI, and method)
	
	// TODO - getBooksByAuthorId (@Mappings, URI, and method)
	
	// TODO - createAuthor (@Mappings, URI, and method)
	
	// TODO - updateAuthor (@Mappings, URI, and method)

	@Autowired
	AuthorService authorService = new AuthorService();

	@GetMapping("/authors")
	public ResponseEntity<Object> getAllAuthor(){
		
		List<Author> author = authorService.findAll();
		
		if(author.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
		return new ResponseEntity<>(author, HttpStatus.OK);		
	}

	@GetMapping("/authors/{id}")
	public ResponseEntity<Author> getAuthorById(@PathVariable("id") Long id) {

		Author author;
		try {
			author = authorService.findById(id);
		} catch (AuthorNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<Author>(author,HttpStatus.OK);
	}

	@PutMapping("authors")
	public ResponseEntity<Author> updateAuthor(@RequestBody Author author) {

		authorService.saveAuthor(author);
		return new ResponseEntity<>(author, HttpStatus.OK);
	}


}
