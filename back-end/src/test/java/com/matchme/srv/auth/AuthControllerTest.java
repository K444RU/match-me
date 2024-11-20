package com.matchme.srv.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnPublicContent() throws Exception {
    this.mockMvc.perform(get("/api/test/all")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("Public Content.")));
  }

  @Test
  void shouldReturnUnauthorized() throws Exception {
    this.mockMvc.perform(get("/api/test/user")).andDo(print()).andExpect(status().isUnauthorized());
    this.mockMvc.perform(get("/api/test/mod")).andDo(print()).andExpect(status().isUnauthorized());
    this.mockMvc.perform(get("/api/test/admin")).andDo(print()).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnUserContent() throws Exception {
    this.mockMvc.perform(get("/api/test/user")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("User Content.")));
  }

  @Test
  void shouldReturnModeratorContent() throws Exception {
    this.mockMvc.perform(get("/api/test/mod")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("Moderator Content.")));
  }

  @Test
  void shouldReturnAdminContent() throws Exception {
    this.mockMvc.perform(get("/api/test/admin")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("Admin Content.")));
  }
}
