package org.springframework.data.demo.repository;

import java.util.List;

import org.springframework.data.demo.domain.Book;

public interface BookShelf {
	
	void add(Book book);
	
	void save(Book book);
	
	Book find(String isbn);
	
	void remove(String isbn);
	
	List<Book> findAll(); 

}
