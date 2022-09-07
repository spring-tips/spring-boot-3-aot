package com.example.demo.reflectiveetal;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

@RegisterReflectionForBinding
record ID(String id) {
}
