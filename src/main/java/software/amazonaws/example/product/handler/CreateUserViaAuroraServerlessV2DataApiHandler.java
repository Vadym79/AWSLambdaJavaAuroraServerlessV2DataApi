// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;

import software.amazonaws.example.product.dao.AuroraServerlessV2DataApiDao;
import software.amazonaws.example.product.entity.User;

public class CreateUserViaAuroraServerlessV2DataApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, User> {

	private static final AuroraServerlessV2DataApiDao auroraServerlessV2DataApiDao = new AuroraServerlessV2DataApiDao();
	


	@Override 
	public User handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		String body = event.getBody();
		if (body != null && !body.isEmpty()) {
			User user = new Gson().fromJson(body, User.class);
			System.out.println("deserialized user "+user);
			if (user != null) {
				return auroraServerlessV2DataApiDao.createUserAndAddressTransactional(user);
			}
		}
		return null;
	}
}