/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.cognix.controllers;

import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.services.UserService;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private User u;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        u = new User("mfnunes", "Marcos Nunes");
        u.setId(1);
        u.setRole("ROLE_ADMIN");
        given(userService.get(1)).willReturn(u);
    }

    @Test
    public void testGetUser() throws Exception {
        given(userService.get(10)).willReturn(new User("mfnunes", "Marcos Nunes"));

        mvc.perform(get("/users/10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo("mfnunes")))
                .andExpect(jsonPath("$.name", equalTo("Marcos Nunes")));
    }

    @Test
    public void testList() throws Exception {
        List<User> result = new ArrayList<>();

        result.add(u);
        result.add(new User("lhrossi", "Luiz Rossi"));
        result.add(new User("asvelasques", "Alan Santos Velasques"));
        given(userService.getAll()).willReturn(result);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                //.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username", equalTo(u.getUsername())))
                .andExpect(jsonPath("$[0].name", equalTo(u.getName())))
                .andExpect(jsonPath("$[0].role", equalTo(u.getRole())))
                .andExpect(jsonPath("$[0].id", equalTo(u.getId())))
                .andExpect(jsonPath("$[1].username", equalTo("lhrossi")))
                .andExpect(jsonPath("$[2].username", equalTo("asvelasques")));
    }

    @Test
    public void testSave() throws Exception {
        String json = "{\"username\":\"mfnunes\",\"name\":\"Marcos Nunes\",\"role\":\"ROLE_ADMIN\", "
                + "\"password\":\"12345\", \"confirmPass\":\"12345\"}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userService).save(argument.capture());

        User result = argument.getValue();
        assertThat(result.getUsername(), equalTo("mfnunes"));
        assertThat(result.getName(), equalTo("Marcos Nunes"));
        assertThat(result.getRole(), equalTo("ROLE_ADMIN"));
        assertThat(result.getPassword(), equalTo("12345"));
    }

    @Test
    public void testNewFailureUsername() throws Exception {
        String json = "{\"username\":\"\",\"name\":\"Marcos Nunes\",\"role\":\"ROLE_ADMIN\", "
                + "\"password\":\"12345\", \"confirmPass\":\"12345\"}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewFailureUsername2() throws Exception {
        String json = "{\"name\":\"Marcos Nunes\",\"role\":\"ROLE_ADMIN\", "
                + "\"password\":\"12345\", \"confirmPass\":\"12345\"}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewFailureName() throws Exception {
        String json = "{\"username\":\"mfnunes\",\"name\":\"\",\"role\":\"ROLE_ADMIN\", "
                + "\"password\":\"12345\", \"confirmPass\":\"12345\"}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewRole() throws Exception {
        String json = "{\"username\":\"mfnunes\",\"name\":\"Marcos Nunes\", "
                + "\"password\":\"12345\", \"confirmPass\":\"12345\"}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewFailurePassConfirmation() throws Exception {
        String json = "{\"username\":\"mfnunes\",\"name\":\"Marcos Nunes\",\"role\":\"ROLE_ADMIN\", "
                + "\"password\":\"12345\", \"confirmPass\":\"12346\"}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testNewFailurePass() throws Exception {
        String json = "{\"username\":\"mfnunes\",\"name\":\"Marcos Nunes\",\"role\":\"ROLE_ADMIN\", "
                + "\"password\":\"123\", \"confirmPass\":\"123\"}";
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEdit() throws Exception {

        String json = "{\"id\":1,\"username\":\"mfnunes\",\"name\":\"Marcos Freitas\",\"role\":\"ROLE_ADMIN\", "
                + "\"password\":\"12345\", \"confirmPass\":\"12345\"}";
        mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userService).save(argument.capture());

        User result = argument.getValue();
        assertThat(result.getId(), equalTo(1));
        assertThat(result.getUsername(), equalTo("mfnunes"));
        assertThat(result.getName(), equalTo("Marcos Freitas"));
        assertThat(result.getRole(), equalTo("ROLE_ADMIN"));
        assertThat(result.getPassword(), equalTo("12345"));
    }
}
