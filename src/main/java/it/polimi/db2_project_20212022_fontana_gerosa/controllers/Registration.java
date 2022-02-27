package it.polimi.db2_project_20212022_fontana_gerosa.controllers;


import it.polimi.db2_project_20212022_fontana_gerosa.beans.User;
import it.polimi.db2_project_20212022_fontana_gerosa.services.UserService;
import it.polimi.db2_project_20212022_fontana_gerosa.utils.ConnectionHandler;
import jakarta.ejb.EJB;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/Registration")
@MultipartConfig
public class Registration extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @EJB(name = "it.polimi.db2_project_20212022_fontana_gerosa.services/UserService")
    private UserService userService = new UserService();

    public Registration() { super();}

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // obtain and escape params
        String email = null;
        String username = null;
        String password = null;
        String repeatedPassword = null;
        username = StringEscapeUtils.escapeJava(request.getParameter("signup_username"));
        email = StringEscapeUtils.escapeJava(request.getParameter("signup_email"));
        password = StringEscapeUtils.escapeJava(request.getParameter("signup_password"));
        repeatedPassword = StringEscapeUtils.escapeJava(request.getParameter("signup_repeated_password"));
        if (email == null || username == null || password == null || repeatedPassword == null || email.isEmpty() || username.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Credentials must be not null");
            return;
        }
        // query db to check existing user
        List<User> byEmail = null;
        List<User> byUsername = null;
        try {
            byEmail = userService.findUserByEmail(email);
            byUsername = userService.findUserByUsername(username);
        } catch (PersistenceException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
            return;
        }
        // If the user doesn't exist
        // return an error status code and message
        if (byEmail.isEmpty() && byUsername.isEmpty() && password.equals(repeatedPassword)) {
            User userToRegister = userService.registerUser(email, username, password);
            response.setStatus(HttpServletResponse.SC_OK);

            //TODO purpose??
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("Welcome, " + userToRegister.getUsername() + "!\nPlease, login.");
        } else if (!byEmail.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.getWriter().println("Email already in use");
        } else if (!byUsername.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.getWriter().println("Username already in use");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            response.getWriter().println("Passwords do not coincide");
        }

    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
