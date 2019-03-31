
package com.lmoumou.aidlservicedemo;
import  com.lmoumou.aidlservicedemo.Book;

interface BookController {
       List<Book> getBookList();

       void addBookInOut(inout Book book);

       void addBookIn(in Book book);

       void addBookOut(out Book book);
}
