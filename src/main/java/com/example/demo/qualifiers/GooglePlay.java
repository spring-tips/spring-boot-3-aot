package com.example.demo.qualifiers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("android")
class GooglePlay implements Market {

}
