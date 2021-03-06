package com.isprox.apitest.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.isprox.apitest.services.impl.ProductServiceImpl;
import com.isprox.server.api.ProductApi;
import com.isprox.server.model.ProductDetail;

@RestController
public class ProductRestController implements ProductApi {

	@Autowired
	ProductServiceImpl service;

	@Override
	public ResponseEntity<Set<ProductDetail>> getProductSimilar(String productId) {
		return service.getSimilarProducts(productId);
	}
}
