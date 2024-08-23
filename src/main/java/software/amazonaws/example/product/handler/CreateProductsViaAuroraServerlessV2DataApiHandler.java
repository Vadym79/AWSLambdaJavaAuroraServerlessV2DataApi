// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.dao.AuroraServerlessV2DataApiDao;
import software.amazonaws.example.product.entity.Product;

public class CreateProductsViaAuroraServerlessV2DataApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final AuroraServerlessV2DataApiDao auroraServerlessV2DataApiDao = new AuroraServerlessV2DataApiDao();
	
	@Override 
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {	
		try {
			String body = event.getBody();
			if (body != null && !body.isEmpty()) {
				List<Product> products = new Gson().fromJson(body, new TypeToken<List<Product>>() {
				}.getType());
				System.out.println("deserialized products " + products);
				if (products != null) {
					List<Product> createdProducts= auroraServerlessV2DataApiDao.createProducts(products);
					System.out.println("created products " + products);
					return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.CREATED)
							.withBody("Products = " + createdProducts + " created");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
					.withBody("Internal Server Error :: " + e.getMessage());
		}
		return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
				.withBody("Internal Server Error ");
	}
	
}