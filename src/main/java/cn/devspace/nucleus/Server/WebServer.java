package cn.devspace.nucleus.Server;

import cn.devspace.nucleus.Manager.SettingManager;
import cn.devspace.nucleus.Message.Log;

import org.springframework.boot.web.server.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.File;
import java.util.Map;

@Configuration
@Component
public class WebServer implements WebMvcConfigurer, ErrorPageRegistrar {

    //拿到静态资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Log.sendLog(new File(Server.RunPath + "Pages/").toURI().toString());
        registry.addResourceHandler("/**").addResourceLocations(new File(Server.RunPath + "Pages/").toURI().toString());
    }

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        Map<String,?> reloads = Server.getSingeYaml(Server.RunPath+"resources/nucleus.yml");
        Object reload = reloads.get("Reload_404");

        ErrorPage[] errorPages = new ErrorPage[1];
        if (reload.equals("Vue")){
            errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
        }else {
            errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
        }
        registry.addErrorPages(errorPages);
    }






}
