package com.isprox.apitest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import com.isprox.apitest.exceptions.ProductNotfoundException;
import com.isprox.apitest.exceptions.ProductServerErrorException;

@ControllerAdvice
public class ProductExceptionController {

	@ExceptionHandler(value = ProductNotfoundException.class)
	public ResponseEntity<Object> exception(ProductNotfoundException exception) {
		return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = HttpClientErrorException.class)
	public ResponseEntity<Object> exception(HttpClientErrorException exception) {
		return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = ProductServerErrorException.class)
	public ResponseEntity<Object> exception(ProductServerErrorException exception) {
		return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
