package com.alejandro.gestordenotas.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alejandro.gestordenotas.entities.User;

@Aspect
@Component
public class UserAspect {

    private static final Logger logger = LoggerFactory.getLogger(UserAspect.class);

    // To create advice that intercepts the save method of the user service file
    @Before("execution(public com.alejandro.gestordenotas.entities.User com.alejandro.gestordenotas.services.UserService.save(com.alejandro.gestordenotas.entities.User))")
    public void trimBeforeSave(JoinPoint joinPoint) {

        logger.info("Aspecto ejecutado antes del método save() ------------------------");

        Object[] args = joinPoint.getArgs(); // Obtiene el argumento del método interceptado
        User userBefore = (User) args[0]; // Cast del argumento al tipo User

        userBefore.setUsername(userBefore.getUsername().trim());
    }

    @Before("execution(* com.alejandro.gestordenotas.services.UserService.update(..))")
    public void trimBeforeUpdate(JoinPoint joinPoint) {

        logger.info("Aspecto ejecutado antes del método update() ------------------------");

        Object[] args = joinPoint.getArgs(); // Obtiene el argumento del método interceptado
        User userBefore = (User) args[1]; // Cast del argumento al tipo User

        userBefore.setUsername(userBefore.getUsername().trim());
    }
}
