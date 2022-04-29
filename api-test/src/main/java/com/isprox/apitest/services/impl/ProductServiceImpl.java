package com.isprox.apitest.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.isprox.apitest.exceptions.ProductNotfoundException;
import com.isprox.server.model.ProductDetail;

@Service
public class ProductServiceImpl {

	@Autowired
	RestTemplate restTemplate;

	@Value("${api.external.productsapi.url}")
	private String url;

	@Value("${api.external.productsapi.getSimilarProducts}")
	private String uriSimilarProducts;

	@Value("${api.external.productsapi.getProductDetails}")
	private String uriProductDetails;

	public ResponseEntity<Set<ProductDetail>> getSimilarProducts(String productId) {
		if (productId != null && !productId.isEmpty()) {
			Set<Integer> setSimilars = this.getProductSimilar(productId);

			if (setSimilars != null && setSimilars.size() > 0) {
				return this.getProductDetail(setSimilars);
			} else {
				throw new ProductNotfoundException();
			}

		} else {
			throw new ProductNotfoundException();
		}
	}

	/*
	 * Call for uriSimilarProduct at the existingApis
	 */
	public Set<Integer> getProductSimilar(String productId) {

		Set<Integer> setPrdDetail = new HashSet<Integer>();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<Set> responseEntity = restTemplate.exchange(url + uriSimilarProducts, HttpMethod.GET, entity,
				Set.class, productId);

		if (responseEntity != null && responseEntity.getStatusCode().value() == 200) {
			return responseEntity.getBody();
		} else {
			throw new ProductNotfoundException();
		}
	}

	public Set<String> getProductSimilarString(String productId) {

		Set<String> setPrdDetail = new HashSet<String>();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<Set> responseEntity = restTemplate.exchange(url + uriSimilarProducts, HttpMethod.GET, entity,
				Set.class, productId);

		if (responseEntity != null && responseEntity.getStatusCode().value() == 200) {
			return responseEntity.getBody();
		} else {
			return setPrdDetail;
		}
	}

	public ResponseEntity<Set<ProductDetail>> getProductDetail(Set<Integer> prdocutIds) {

		List<CompletableFuture<ProductDetail>> cfs = new ArrayList<CompletableFuture<ProductDetail>>();
		for (Integer productId : prdocutIds) {
			cfs.add(getAsyncProductDetail(productId));
		}

		CompletableFuture<?> allFutures = CompletableFuture.allOf(cfs.toArray(new CompletableFuture[cfs.size()]));

		try {
			allFutures.get();
			if (allFutures.isDone()) {

				Set<ProductDetail> sPrdDetail = new HashSet<ProductDetail>();
				for (CompletableFuture<ProductDetail> cf : cfs) {
					ProductDetail prd = cf.get();

					if (prd != null) {
						sPrdDetail.add(prd);
					}
				}
				return ResponseEntity.ok(sPrdDetail);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ProductDetail getProductDetail(Integer productId) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<ProductDetail> responseEntity = restTemplate.exchange(url + uriProductDetails, HttpMethod.GET,
				entity, ProductDetail.class, productId);

		if (responseEntity != null && responseEntity.getStatusCode().value() == 200) {
			return responseEntity.getBody();
		} else {
			return null;
		}
	}

	private CompletableFuture<ProductDetail> getAsyncProductDetail(Integer productId) {
		return CompletableFuture.supplyAsync(() -> {
			;
			return getProductDetail(productId);
		}).handleAsync((msg, ex) -> {
			if (ex != null && ex.getCause() != null) {
				Throwable cosa = ex.getCause();
				if (cosa instanceof HttpClientErrorException
						&& (((HttpClientErrorException) cosa).getRawStatusCode() == 404
								|| ((HttpClientErrorException) cosa).getRawStatusCode() == 500)) {
					return null;
				}
				return msg;
			}
			return msg;
		});
	}
}
