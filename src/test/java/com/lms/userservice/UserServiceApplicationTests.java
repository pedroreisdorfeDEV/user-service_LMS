package com.lms.userservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
class UserServiceApplicationTests {

    @Test
    void applicationClassNameIsStable() {
        assertEquals("UserServiceApplication", UserServiceApplication.class.getSimpleName());
    }
}
