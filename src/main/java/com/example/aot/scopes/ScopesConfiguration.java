package com.example.aot.scopes;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

@Configuration
class ScopesConfiguration {
}


@Controller
@ResponseBody
class ContextHttpController {

    private final RequestContext context ;

    @Lazy
    ContextHttpController(RequestContext context) {
        this.context = context;
    }

    @GetMapping ("/scopes/context")
    String uuid (){
        return this.context.getUuid() ;
    }
}

@Component
@Scope (WebApplicationContext.SCOPE_REQUEST)
class RequestContext {

    private final String uuid = UUID.randomUUID().toString();

    public String getUuid() {
        return uuid;
    }
}