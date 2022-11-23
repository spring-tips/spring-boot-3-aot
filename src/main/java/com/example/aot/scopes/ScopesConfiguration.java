package com.example.aot.scopes;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

@Configuration
class ScopesConfiguration {
}

// <1>
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
class RequestContext {

    private final String uuid = UUID.randomUUID().toString(); //<2>

    public String getUuid() {
        return uuid;
    }
}

@RestController
class ContextHttpController {

    private final RequestContext context;

    @Lazy // <3>
    ContextHttpController(RequestContext context) {
        this.context = context;
    }

    @GetMapping("/scopes/context")
    String uuid() {
        return this.context.getUuid();
    }
}
