package com.isprox.apitest.services;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.isprox.server.model.ProductDetail;

@Service
public interface ProductService {

	public ResponseEntity<Set<ProductDetail>> getSimilarProducts(String productId);

	public Set<String> getProductSimilar(String productId);

	public ResponseEntity<Set<ProductDetail>> getProductDetail(Set<String> prdocutIds);

	public ProductDetail getProductDetail(String productId);
}
