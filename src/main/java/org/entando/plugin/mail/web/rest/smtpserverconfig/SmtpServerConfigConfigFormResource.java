package org.entando.plugin.mail.web.rest.smtpserverconfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.entando.plugin.mail.domain.SmtpServerConfig;
import org.entando.plugin.mail.web.rest.SmtpServerConfigResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/form")
public class SmtpServerConfigConfigFormResource {

    private final Logger log = LoggerFactory.getLogger(SmtpServerConfigConfigFormResource.class);

    @GetMapping(value = "/SmtpServerConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJsonSchemaFormConfiguration() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

        JsonNode jsonSchema = jsonSchemaGenerator.generateJsonSchema(SmtpServerConfig.class);

        return objectMapper.writeValueAsString(jsonSchema);
    }
}
