// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.util.Optional;

import org.crac.Core;
import org.crac.Resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import software.amazonaws.example.product.dao.AuroraServerlessV2DataApiDao;
import software.amazonaws.example.product.entity.Product;

public class GetProductByIdViaAuroraServerlessV2DataApiWithPrimingHandler implements RequestHandler<APIGatewayProxyRequestEvent, Optional<Product>>, Resource {

	private static final AuroraServerlessV2DataApiDao auroraServerlessV2DataApiDao = new AuroraServerlessV2DataApiDao();

	public GetProductByIdViaAuroraServerlessV2DataApiWithPrimingHandler() {
		Core.getGlobalContext().register(this);
	}

	@Override
	public void beforeCheckpoint(org.crac.Context<? extends Resource> context) throws Exception {
		auroraServerlessV2DataApiDao.getProductById("0");
	}

	@Override
	public void afterRestore(org.crac.Context<? extends Resource> context) throws Exception {

	}

	@Override
	public Optional<Product> handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		final String id = event.getPathParameters().get("id");
		return auroraServerlessV2DataApiDao.getProductById(id);
	}
}