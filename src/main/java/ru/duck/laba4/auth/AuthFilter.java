package ru.duck.laba4.auth;

import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import ru.duck.laba4.database.UserDatabase;

import java.net.URI;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @EJB
    private UserDatabase userDatabase;

    @Override
    public void filter(ContainerRequestContext context) {
        try {
            String token = context.getCookies().get("token").getValue();

            if (!validateToken(token))
                throw new AuthException(AuthStatus.INVALID_TOKEN);
        } catch (Exception ex) {
            context.abortWith(Response
                    .seeOther(URI.create("/auth"))
                    .build()
            );
        }
    }

    private boolean validateToken(String token) {
        return token != null && userDatabase.find("token", token).size() > 0;
    }
}