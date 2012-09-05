package org.modelmapper.functional.circular;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Tests the handling of circular references. Model taken from
 * https://github.com/jhalterman/modelmapper/issues/2.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CircularDependencies6 extends AbstractTest {
  Library lib;
  Book book1, book2;
  Author author1, author2, author3;

  static class Library {
    String id;
    List<Book> books;
    List<Author> authors;
  }

  static class Book {
    String id;
    List<Author> authors;
  }

  static class Author {
    String id;
    List<Book> books;
  }

  static class DestLibrary {
    String id;
    List<DestBook> books;
    List<DestAuthor> authors;
  }

  static class DestBook {
    String id;
    List<DestAuthor> authors;
  }

  static class DestAuthor {
    String id;
    List<DestBook> books;
  }

  {
    lib = new Library();
    lib.id = "lib1";

    author1 = new Author();
    author1.id = "author1";
    author2 = new Author();
    author2.id = "author2";
    author3 = new Author();
    author3.id = "author3";

    book1 = new Book();
    book1.id = "book1";
    book1.authors = Arrays.asList(author1, author2);
    book2 = new Book();
    book2.id = "book2";
    book2.authors = Arrays.asList(author3);

    author1.books = Arrays.asList(book1);
    author2.books = Arrays.asList(book1);
    author3.books = Arrays.asList(book2);

    lib.books = Arrays.asList(book1, book2);
    lib.authors = Arrays.asList(author1, author2, author3);
  }

  public void shouldMapBooks() {
    DestBook dBook1 = modelMapper.map(book1, DestBook.class);
    DestAuthor author1 = dBook1.authors.get(0);
    DestAuthor author2 = dBook1.authors.get(1);
    assertEquals(dBook1.id, "book1");
    assertEquals(author1.id, "author1");
    assertEquals(author1.books.get(0), dBook1);
    assertEquals(author2.id, "author2");
    assertEquals(author2.books.get(0), dBook1);

    DestBook dBook2 = modelMapper.map(book2, DestBook.class);
    DestAuthor author3 = dBook2.authors.get(0);
    assertEquals(dBook2.id, "book2");
    assertEquals(author3.id, "author3");
    assertEquals(author3.books.get(0), dBook2);
  }

  public void shouldMapAuthor() {
    DestAuthor dAuthor1 = modelMapper.map(author1, DestAuthor.class);
    assertEquals(dAuthor1.books.size(), 1);
    DestBook dAuthor1Book = dAuthor1.books.get(0);
    assertEquals(dAuthor1.id, "author1");
    assertEquals(dAuthor1Book.id, "book1");
    assertEquals(dAuthor1Book.authors.get(0), dAuthor1);

    DestAuthor dAuthor2 = modelMapper.map(author2, DestAuthor.class);
    assertEquals(dAuthor2.books.size(), 1);
    DestBook dAuthor2Book = dAuthor2.books.get(0);
    assertEquals(dAuthor2.id, "author2");
    assertEquals(dAuthor2Book.id, "book1");
    assertEquals(dAuthor2Book.authors.get(1), dAuthor2);

    DestAuthor dAuthor3 = modelMapper.map(author3, DestAuthor.class);
    assertEquals(dAuthor3.books.size(), 1);
    DestBook dAuthor3Book = dAuthor3.books.get(0);
    assertEquals(dAuthor3.id, "author3");
    assertEquals(dAuthor3Book.id, "book2");
    assertEquals(dAuthor3Book.authors.size(), 1);
    assertEquals(dAuthor3Book.authors.get(0), dAuthor3);
  }

  public void shouldMapLibrary() {
    DestLibrary dLib = modelMapper.map(lib, DestLibrary.class);
    assertEquals(dLib.books.size(), 2);
    assertEquals(dLib.authors.size(), 3);
    assertEquals(dLib.id, "lib1");
    
    DestBook dBook1 = dLib.books.get(0);
    DestBook dBook2 = dLib.books.get(1);
    DestAuthor dAuthor1 = dLib.authors.get(0);
    DestAuthor dAuthor2 = dLib.authors.get(1);
    DestAuthor dAuthor3 = dLib.authors.get(2);
    
    assertEquals(dBook1.id, "book1");
    assertEquals(dBook2.id, "book2");
    assertEquals(dAuthor1.id, "author1");
    assertEquals(dAuthor2.id, "author2");
    assertEquals(dAuthor3.id, "author3");
    
    assertEquals(dBook1.authors, Arrays.asList(dAuthor1, dAuthor2));
    assertEquals(dBook2.authors, Arrays.asList(dAuthor3));
    
    assertEquals(dAuthor1.books, Arrays.asList(dBook1));
    assertEquals(dAuthor2.books, Arrays.asList(dBook1));
    assertEquals(dAuthor3.books, Arrays.asList(dBook2));
  }
}
