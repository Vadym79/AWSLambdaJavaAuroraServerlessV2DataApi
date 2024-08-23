// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.dao.AuroraServerlessV2DataApiDao;
import software.amazonaws.example.product.entity.User;

public class CreateUserViaAuroraServerlessV2DataApiHandler
		implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final AuroraServerlessV2DataApiDao auroraServerlessV2DataApiDao = new AuroraServerlessV2DataApiDao();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		try {
			String body = event.getBody();
			if (body != null && !body.isEmpty()) {
				User user = new Gson().fromJson(body, User.class);
				System.out.println("deserialized user " + user);
				if (user != null) {
					User createdUser = auroraServerlessV2DataApiDao.createUserAndAddressTransactional(user);
					System.out.println("created user " + createdUser);
					return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.CREATED)
							.withBody("User = " + createdUser + " created");
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