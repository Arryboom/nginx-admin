package com.jslsolucoes.nginx.admin.agent.client.api.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.jslsolucoes.nginx.admin.agent.client.RestClient;
import com.jslsolucoes.nginx.admin.agent.client.api.NginxAgentClientApi;
import com.jslsolucoes.nginx.admin.agent.model.response.NginxAuthenticationFailResponse;
import com.jslsolucoes.nginx.admin.agent.model.response.NginxExceptionResponse;
import com.jslsolucoes.nginx.admin.agent.model.response.NginxOperationalSystemDescriptionResponse;
import com.jslsolucoes.nginx.admin.agent.model.response.NginxResponse;

public class NginxOperationalSystemInfo implements NginxAgentClientApi {

	private final ScheduledExecutorService scheduledExecutorService;
	private final String authorization;
	private String endpoint;

	public NginxOperationalSystemInfo(ScheduledExecutorService scheduledExecutorService, String authorization,
			String endpoint) {
		this.scheduledExecutorService = scheduledExecutorService;
		this.authorization = authorization;
		this.endpoint = endpoint;
	}

	public CompletableFuture<NginxResponse> operationalSystemInfo() {
		return CompletableFuture.supplyAsync(() -> {
			try (RestClient restClient = RestClient.build()) {
				WebTarget webTarget = restClient.target(endpoint);
				Response response = webTarget.path("/admin/operationalSystemInfo").request()
						.header(HttpHeader.AUTHORIZATION, authorization).get();
				if (response.getStatusInfo().equals(Status.OK)) {
					return response.readEntity(NginxOperationalSystemDescriptionResponse.class);
				}
				if (response.getStatusInfo().equals(Status.FORBIDDEN)) {
					return response.readEntity(NginxAuthenticationFailResponse.class);
				} else {
					return response.readEntity(NginxExceptionResponse.class);
				}
			} catch (Exception e) {
				return new NginxExceptionResponse(e);
			}
		}, scheduledExecutorService);
	}

}