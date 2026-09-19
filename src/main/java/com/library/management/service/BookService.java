package com.library.management.service;

import com.library.management.entity.Book;
import com.library.management.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get book by ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found with id: " + id));
    }

    // Create book
    public Book createBook(Book book) {

        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new RuntimeException(
                    "Book already exists with ISBN: " + book.getIsbn()
            );
        }

        // Initially all copies are available
        book.setAvailableCopies(book.getTotalCopies());

        return bookRepository.save(book);
    }

    // Update book
    public Book updateBook(Long id, Book bookDetails) {

        Book book = getBookById(id);

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setCategory(bookDetails.getCategory());

        /*
         * Keep track of currently issued copies.
         * Example:
         * total = 10
         * available = 7
         * issued = 3
         *
         * If total becomes 12:
         * available = 12 - 3 = 9
         */
        int issuedCopies =
                book.getTotalCopies() - book.getAvailableCopies();

        int newAvailableCopies =
                bookDetails.getTotalCopies() - issuedCopies;

        if (newAvailableCopies < 0) {
            throw new RuntimeException(
                    "Total copies cannot be less than issued copies"
            );
        }

        book.setAvailableCopies(newAvailableCopies);

        return bookRepository.save(book);
    }

    // Delete book
    public void deleteBook(Long id) {

        Book book = getBookById(id);

        bookRepository.delete(book);
    }

    // Search by title
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // Search by author
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    // Find books by category
    public List<Book> getBooksByCategory(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }
}