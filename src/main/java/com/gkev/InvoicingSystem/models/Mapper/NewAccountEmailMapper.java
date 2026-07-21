package com.gkev.InvoicingSystem.models.Mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
public class NewAccountEmailMapper {

    @Value("${app.frontend.login-url:http://localhost:5173/auth}")
    private String loginUrl;

    public Context setData(String firstName, String email, String tempPassword, String role, String loginToken) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("email", email);
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("role", role);
        context.setVariable("loginUrl", loginUrl);
        context.setVariable("loginToken", loginToken);
        return context;
    }
}
