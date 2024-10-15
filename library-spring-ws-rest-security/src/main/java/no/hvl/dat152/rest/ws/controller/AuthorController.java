/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

	// TODO authority annotation
    @Autowired
    private AuthorService authorService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/authors")
    public ResponseEntity<Object> getAllAuthors() {
        List<Author> authors = authorService.findAll();

        if (authors.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable("id") long id) throws AuthorNotFoundException {
        Author author = authorService.findById(id);

        if (author == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<Author>(author, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        Author nAuthor = authorService.saveAuthor(author);

        return new ResponseEntity<>(nAuthor, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> updateAuthor(@RequestBody Author author,@PathVariable ("id") long id) {
        if (author == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try {
            Author newauthor = authorService.updateAuthor(author, id);
            return new ResponseEntity<>(newauthor, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/authors/{id}/books") //new
    public ResponseEntity<Object> getBooksByAuthorId(@PathVariable("id") Long id) throws BookNotFoundException {
        try {
            Set<Book> authors = authorService.findBooksByAuthorId(id);
            return new ResponseEntity<>(authors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
