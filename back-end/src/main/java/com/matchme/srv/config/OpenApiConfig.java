package com.matchme.srv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
  static {
    // use Reusable Enums for Swagger generation:
    // see https://springdoc.org/#how-can-i-apply-enumasref-true-to-all-enums
    io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;

    // Testing releaseplease
  }

  // ... you can also describe your api bellow
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
      .info(
        new Info()
          .title("Blind API")
          .description("kood/Jõhvi match-me task API")
          .version("v0.0.1")
      )
      .externalDocs(
        new ExternalDocumentation()
          .description("Gitea")
          .url("https://gitea.kood.tech/karlrometsomelar/match-me")
      );
  }
}
