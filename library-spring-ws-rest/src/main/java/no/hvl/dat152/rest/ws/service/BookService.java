/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UpdateBookFailedException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * @author tdoy
 */
@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	
	public Book saveBook(Book book) {
		return bookRepository.save(book);

	}
	
	public List<Book> findAll(){
		
		return (List<Book>) bookRepository.findAll();
		
	}
	
	
	public Book findByISBN(String isbn) throws BookNotFoundException {
		
		Book book = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));
		
		return book;
	}
	
	/**
	 * Updates an existing book in the repository.
	 *
	 * @param book the book object containing updated information
	 * @return the updated book object
	 */
	public Book updateBook(Book book, String isbn) throws UpdateBookFailedException{
		if(book == null){
			throw new UpdateBookFailedException("No book was provided, update not possible");
		}

		try {
			Book newBook = findByISBN(isbn);
			newBook.setId(book.getId());
			newBook.setIsbn(isbn);
			newBook.setTitle(book.getTitle());
			newBook.setAuthors(book.getAuthors());
		} catch (Exception e) {
			throw new UpdateBookFailedException("Book could not be updated");
		}

		return book;
	}
	
	public List<Book> findAllPaginate(Pageable page) {
		return bookRepository.findAll(page).getContent();
	}
	
	public Set<Author> findAuthorsOfBookByISBN(String isbn) throws BookNotFoundException {
		try {
			Book book = bookRepository.findBookByISBN(isbn);
			return book.getAuthors();
		} catch (Exception e) {
			throw new BookNotFoundException("Book with id = "+isbn+" was not found!");
		}
	}
	
	public void DeleteById(Long id) throws BookNotFoundException {
		try {
			bookRepository.deleteById(id);
		} catch (Exception e) {
			throw new BookNotFoundException("Book with id = "+id+" could not be deleted!");
		}
	}
	
	/**
	 * Deletes a book from the repository based on its ISBN.
	 *
	 * @param isbn the ISBN of the book to be deleted
	 */
	public void deleteByISBN(String isbn) throws BookNotFoundException {
		Book book = null;
		try {
			book = bookRepository.findBookByISBN(isbn);
		}catch(Exception e) {
			throw new BookNotFoundException("Book with isbn = "+isbn+" not found!");
		}
		bookRepository.delete(book);
	}
	
}
