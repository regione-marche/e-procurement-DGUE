package it.maggioli.eldasoft.dgue.msdgueserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "M-DGUE", version = "1.0", description = "M-DGUE Controllers"))
public class MsDgueServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsDgueServerApplication.class, args);
    }
}
