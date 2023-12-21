package com.colegiado.sistemacolegiado.controllers;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

//    public String getErrorPath() {
//        return "/error";
//    }
//
//    @RequestMapping("/error")
//    public String handleError(WebRequest webRequest, Model model) {
//        // Obter detalhes do erro
//        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.STACK_TRACE));
//
//        // Adicionar atributos do erro ao modelo
//        model.addAttribute("status", errorAttributes.get("status"));
//        model.addAttribute("error", errorAttributes.get("error"));
//        model.addAttribute("message", errorAttributes.get("message"));
//        model.addAttribute("trace", errorAttributes.get("trace"));
//
//        // Retornar o nome do template Thymeleaf para exibir a página de erro personalizada
//        return "error"; // Nome do template HTML/Thymeleaf
//    }
}
