package org.springframework.data.demo.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.demo.domain.Author;
import org.springframework.data.demo.domain.Book;
import org.springframework.data.demo.repository.BookShelf;
import org.springframework.data.demo.repository.DbHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Autowired
	BookShelf bookShelf;
	
	@Autowired
	DbHelper dbHelper;
	
	@InitBinder
	public void initBinder(Locale locale, WebDataBinder dataBinder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		dataBinder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		
		model.addAttribute("version", "The MongoTemplate Version for Mongo Boston 2011");
		
		model.addAttribute("bookList", bookShelf.findAll() );
		
		return "home";
	}
	
	/**
	 * Add new book.
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newBook(Model model) {
		
		Book book = new Book();
		book.setAuthor(new Author());
		model.addAttribute("book", book );
		
		return "addBook";
	}

	/**
	 * Add new book.
	 */
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String addBook(@ModelAttribute("book") Book newBook, BindingResult result, SessionStatus status, HttpServletRequest request) {
		
		if (request.getParameter("_cancel") != null) {
			return "redirect:/";
		}
		if (newBook != null && (newBook.getIsbn() == null || newBook.getIsbn().length() <= 0)) {
			ObjectError error = new ObjectError("book.isbn", "ISBN can't be empty");
			result.addError(error);
		}
		if (result.hasErrors()) {
			return	"addBook";
		}
		if (newBook != null) {
			status.setComplete();
			bookShelf.add(newBook);
		}
		
		return "redirect:/";
	}

	/**
	 * Edit a book.
	 */
	@RequestMapping(value = "/edit/{isbn}", method = RequestMethod.GET)
	public String editBook(@PathVariable("isbn") String isbn, Model model) {
		
		Book book = bookShelf.find(isbn);
		model.addAttribute("book", book );
		
		return "editBook";
	}

	/**
	 * Save and edited book.
	 */
	@RequestMapping(value = "/edit/{isbn}", method = RequestMethod.POST)
	public String modifyBook(@PathVariable("isbn") String isbn, @ModelAttribute("book") Book book, BindingResult result, SessionStatus status, HttpServletRequest request) {
		
		if (request.getParameter("_cancel") != null) {
			return "redirect:/";
		}
		if (request.getParameter("_delete") != null) {
			bookShelf.remove(isbn);
			return "redirect:/";
		}
		if (result.hasErrors()) {
			return "editBook";
		}
		if (book != null) {
			status.setComplete();
			bookShelf.save(book);
		}
		
		return "redirect:/";
	}

	/**
	 * Utility methods
	 */
	@RequestMapping(value={"/dump"}, method=RequestMethod.GET)
	public String dump(Model model) {
		model.addAttribute("bookdata", dbHelper.getDump(Book.class));
		model.addAttribute("authordata", dbHelper.getDump(Author.class));
		return "dump";
	}

	@RequestMapping(value={"/clear"}, method=RequestMethod.GET)
	public String clear(Model model) {
		dbHelper.clear();
		return "redirect:/";
	}
}
