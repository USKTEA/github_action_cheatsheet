package com.usktea.plainold.controllers;

import com.usktea.plainold.applications.user.CountUserService;
import com.usktea.plainold.applications.user.CreateUserService;
import com.usktea.plainold.applications.user.EditUserService;
import com.usktea.plainold.applications.user.GetUserService;
import com.usktea.plainold.dtos.CreateUserRequest;
import com.usktea.plainold.dtos.EditUserRequest;
import com.usktea.plainold.exceptions.UserNotExists;
import com.usktea.plainold.exceptions.UsernameAlreadyInUse;
import com.usktea.plainold.exceptions.UsernameNotMatch;
import com.usktea.plainold.models.user.Username;
import com.usktea.plainold.models.user.Users;
import com.usktea.plainold.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUserService getUserService;

    @MockBean
    private EditUserService editUserService;

    @MockBean
    private CountUserService countUserService;

    @MockBean
    private CreateUserService createUserService;

    @SpyBean
    private JwtUtil jwtUtil;

    @Test
    void whenUserExists() throws Exception {
        Username username = new Username("tjrxo1234@gmail.com");

        String token = jwtUtil.encode(username.value());

        given(getUserService.find(username))
                .willReturn(Users.fake(username));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"username\"")
                ));
    }

    @Test
    void whenUserNotExists() throws Exception {
        Username username = new Username("notExists@gmail.com");

        String token = jwtUtil.encode(username.value());

        given(getUserService.find(username)).willThrow(UserNotExists.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEditUserSuccess() throws Exception {
        Username username = new Username("tjrxo1234@gmail.com");
        String token = jwtUtil.encode(username.value());

        given(editUserService.edit(any(Username.class), any(EditUserRequest.class)))
                .willReturn(username);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"username\": \"tjrxo1234@gmail.com\", " +
                        "\"nickname\":\"김뚜루\"" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"nickname\"")
                ));
    }

    @Test
    void whenEditUserFailed() throws Exception {
        Username username = new Username("tjrxo1234@gmail.com");
        String token = jwtUtil.encode(username.value());

        given(editUserService.edit(any(Username.class), any(EditUserRequest.class)))
                .willThrow(UsernameNotMatch.class);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\": \"notTjrxo1234@gmail.com\", " +
                                "\"nickname\":\"김뚜루\"" +
                                "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSomeEditRequestInformationNotExists() throws Exception {
        Username username = new Username("tjrxo1234@gmail.com");
        String token = jwtUtil.encode(username.value());

        mockMvc.perform(MockMvcRequestBuilders.patch("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\": \"\", " +
                                "\"nickname\":\"김뚜루\"" +
                                "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void countUserSuccess() throws Exception {
        String username = "tjrxo1234@gmail.com";
        Integer counts = 1;

        given(countUserService.count(new Username(username))).willReturn(counts);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/users?username=%s", username)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"count\"")
                ));
    }

    @Test
    void countUserFailed() throws Exception {
        String username = "invalid";

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/users?username=%s", username)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateUserSuccess() throws Exception {
        String nickname = "김뚜루";
        String username = "tjrxo1234@gmail.com";
        String password = "Password1234!";

        given(createUserService.create(any(CreateUserRequest.class)))
                .willReturn(new Username(username));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"nickname\": \"" + nickname + "\", " +
                        "\"username\": \"" + username + "\", " +
                        "\"password\": \"" + password + "\"" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(
                        containsString("\"username\"")
                ));
    }

    @Test
    void whenCreateUserAnyOfRequestInformationIsInvalid() throws Exception {
        String username = "invalid";
        String nickname = "김뚜루";
        String password = "Password1234!";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"nickname\": \"" + nickname + "\", " +
                                "\"username\": \"" + username + "\", " +
                                "\"password\": \"" + password + "\"" +
                                "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateUserAnyOfRequestInformationIsNotExists() throws Exception {
        String username = "";
        String nickname = "김뚜루";
        String password = "Password1234!";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"nickname\": \"" + nickname + "\", " +
                                "\"username\": \"" + username + "\", " +
                                "\"password\": \"" + password + "\"" +
                                "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUsernameAlreadyInUse() throws Exception {
        String nickname = "김뚜루";
        String username = "alreadyInUse@gmail.com";
        String password = "Password1234!";

        given(createUserService.create(any(CreateUserRequest.class)))
                .willThrow(UsernameAlreadyInUse.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"nickname\": \"" + nickname + "\", " +
                                "\"username\": \"" + username + "\", " +
                                "\"password\": \"" + password + "\"" +
                                "}"))
                .andExpect(status().isBadRequest());
    }
}
