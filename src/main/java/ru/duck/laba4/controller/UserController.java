package ru.duck.laba4.controller;


import com.password4j.Hash;
import com.password4j.Password;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.duck.laba4.auth.AuthException;
import ru.duck.laba4.database.UserDatabase;
import ru.duck.laba4.entity.User;
import ru.duck.laba4.auth.AuthStatus;
import ru.duck.laba4.util.StringUtil;

import static ru.duck.laba4.auth.AuthStatus.*;

@Singleton
@Lock(LockType.WRITE)
@Path("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {

    @EJB
    UserDatabase userDatabase;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authentication(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("registration") String registration,
            @CookieParam("token") Cookie cookie
    ) {
        try {
            AuthStatus status = process(
                    username,
                    password,
                    Boolean.parseBoolean(registration),
                    cookie
            );

            if (status != SUCCESS) throw new AuthException(status);

            String token = generateToken();
            User user = userDatabase.findByKey(username);

            user.setToken(token);
            userDatabase.update(user);

            return Response.ok()
                    .cookie(new NewCookie.Builder("token")
                            .value(token)
                            .build())
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @DELETE
    public Response logout(@CookieParam("token") Cookie cookie) {
        userDatabase.find("token", cookie.getValue())
                .forEach(user -> {
                    user.setToken(null);
                    userDatabase.update(user);
                });

        return Response.ok().build();
    }

    private AuthStatus process(String username, String password, boolean registration, Cookie cookie) {
        if (cookie != null && cookie.getValue() != null &&
                userDatabase.find("token", cookie.getValue()).size() > 0)
            return ALREADY_AUTHENTICATED;

        if (!validateCredentials(username, password))
            return INVALID_CREDENTIALS;

        return registration ? register(username, password) : login(username, password);
    }

    private AuthStatus register(String username, String password) {
        if (userDatabase.findByKey(username) != null)
            return USER_ALREADY_EXISTS;

        Hash hash = Password.hash(password)
                .addRandomSalt(8)
                .withArgon2();

        userDatabase.save(new User(username, hash.getResult(), hash.getSalt(), null));

        return SUCCESS;
    }

    private AuthStatus login(String username, String password) {
        User user = userDatabase.findByKey(username);

        if (user == null) return USER_DOES_NOT_EXIST;

        boolean verified = Password.check(password, user.getPasswordHash())
                .addSalt(user.getPasswordSalt())
                .withArgon2();

        if (!verified) return WRONG_PASSWORD;

        return SUCCESS;
    }

    private boolean validateCredentials(String username, String password) {
        return username != null && password != null &&
                username.matches("[a-zA-Z0-9]{6,}") &&
                password.matches("[!-~]{6,}");
    }

    private String generateToken() {
        return StringUtil.randomString(48);
    }

}