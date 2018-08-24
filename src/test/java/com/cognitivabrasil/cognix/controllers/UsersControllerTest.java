/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.cognix.controllers;

import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.services.UserService;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(MockitoJUnitRunner.class)
public class UsersControllerTest {

    private MockMvc mvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UsersController controller;

    @Before
    public void setup(){
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetUser() throws Exception {
        given(userService.get(10)).willReturn(new User("mfnunes", "Marcos Nunes"));

        mvc.perform(MockMvcRequestBuilders.get("/users/10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo("mfnunes")))
                .andExpect(jsonPath("$.name", equalTo("Marcos Nunes")));
    }
}
