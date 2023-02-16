package com.sandbox.settlement.common.security;

import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
    @Value("${server.servlet.session.timeout}")
    private int intSessionTimeOut;

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		httpSessionEvent.getSession().setMaxInactiveInterval(intSessionTimeOut);

		return;
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		return;
	}
}
