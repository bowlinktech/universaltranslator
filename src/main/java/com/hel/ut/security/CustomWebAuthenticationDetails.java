package com.hel.ut.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class CustomWebAuthenticationDetails extends
        WebAuthenticationDetailsSource {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new MyAuthenticationDetails(context);
    }

    @SuppressWarnings("serial")
    class MyAuthenticationDetails extends WebAuthenticationDetails {

        private final String loginAsUser;

        public MyAuthenticationDetails(HttpServletRequest request) {
            super(request);
            this.loginAsUser = request.getParameter("loginAsUser");
        }

        public String getLoginAsUser() {
            return loginAsUser;
        }
    }

}
