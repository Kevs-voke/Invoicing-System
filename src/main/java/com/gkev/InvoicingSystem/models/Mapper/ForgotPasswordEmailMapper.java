package com.gkev.InvoicingSystem.models.Mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
public class ForgotPasswordEmailMapper {

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    public Context setData(String firstName, String email, String token) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("email", email);
        context.setVariable("resetLink", resetPasswordUrl + "?token=" + token);
        return context;
    }
}