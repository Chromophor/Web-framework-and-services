/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
		
	
	public Author findById(long id) throws AuthorNotFoundException {
		
		Author author = authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));
		
		return author;
	}
	
	public Author saveAuthor(Author author) {
		authorRepository.save(author);
		return author;
	}
		
	
	public Author updateAuthor(Long id, Author author) throws AuthorNotFoundException {
		Author newAuthor = authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("Author with the id: " + id + " not found!"));

		newAuthor.setFirstname(author.getFirstname());
		newAuthor.setLastname(author.getLastname());
		newAuthor.setBooks(author.getBooks());
		return newAuthor;
	}
	
	
	public List<Author> findAll() {
		return (List<Author>) authorRepository.findAll();
	}
	
	
	// TODO public void deleteById(Long id) throws AuthorNotFoundException 
	public void deletebyId(Long id) throws AuthorNotFoundException {
		try {
			authorRepository.deleteById(id);
		} catch (Exception e) {
			throw new AuthorNotFoundException("Author with id: "+id+" could not be deleted");
		}
	}
	
	// TODO public Set<Book> findBooksByAuthorId(Long id)
	public Set<Book> findBooksByAuthorId(Long id) throws AuthorNotFoundException {
		Author author = findById(id);
		return author.getBooks();	
	}

}
