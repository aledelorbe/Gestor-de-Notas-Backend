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

    // To create advice that intercepts the 'save' method of the user service file
    @Before("execution(* com.alejandro.gestordenotas.services.UserServiceImp.save(..))")
    public void trimBeforeSave(JoinPoint joinPoint) {

        logger.info("Aspect executing before the save method () ------------------------");

        Object[] args = joinPoint.getArgs(); // Get the argument of the method to be intercepted
        User userBefore = (User) args[0]; // Cast the argument to type User

        this.cleanSpaces(userBefore);
    }

    // To create advice that intercepts the 'update' method for the user entity
    @Before("execution(* com.alejandro.gestordenotas.services.UserServiceImp.update(..))")
    public void trimBeforeUpdate(JoinPoint joinPoint) {

        logger.info("Aspect executing before the update method () ------------------------");

        Object[] args = joinPoint.getArgs(); // Get the argument of the method to be intercepted
        User userBefore = (User) args[1]; // Cast the argument to type User

        this.cleanSpaces(userBefore);
    }

    // To remove the blanks in this object
    private void cleanSpaces(User userBefore) {

        userBefore.setUsername(userBefore.getUsername().trim());

    }
}
