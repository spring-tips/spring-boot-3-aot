package bootiful.aot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/* todo doesn't work yet */
@Configuration
@EnableAspectJAutoProxy
class AopConfiguration {

	@Bean
	LoggingAspect loggingAspect() {
		return new LoggingAspect();
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> aspectListener(Announcer announcer) {
		return event -> announcer.announce("hello, world");
	}

}

@Service
@Slf4j
class Announcer {

	@Logged
	public void announce(String message) {
		log.info(message.toUpperCase());
	}

}

@Slf4j
@Aspect
class LoggingAspect {

	@Around("@annotation(bootiful.aot.aop.Logged)")
	public Object logMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		log.info("LoggingAspect[begin]");
		var result = proceedingJoinPoint.proceed();
		log.info("LoggingAspect[end]");
		return result;
	}

}

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Logged {

}
