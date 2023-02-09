package cn.devspace.nucleus.Server;

import cn.devspace.nucleus.Message.Log;
import org.springframework.boot.web.server.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Map;

@Configuration
@Component
public class WebServer implements WebMvcConfigurer {

    //拿到静态资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(new File(Server.RunPath + "Pages/").toURI().toString());
    }

//    @Override
//    public void registerErrorPages(ErrorPageRegistry registry) {
//        Map<String,?> reloads = Server.getSingeYaml(Server.RunPath+"resources/nucleus.yml");
//        Object reload = reloads.get("Reload_404");
//        if (reload == null){
//            reload = "none";
//        }
//        ErrorPage[] errorPages = new ErrorPage[1];
//        Log.sendLog(reload.toString());
//        if (reload.equals("Vue")){
//            errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
//        }else {
//            errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
//        }
//        registry.addErrorPages(errorPages);
//    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        Map<String,?> reloads = Server.getSingeYaml(Server.RunPath+"resources/nucleus.yml");
        Object reload = reloads.get("Reload.404");
        if (reload == null){
            reload = "none";
        }
        if (reload.equals("Vue") || reload.equals("vue") || reload.equals("VUE") || reload.equals("React") || reload.equals("react") || reload.equals("REACT") || reload.equals("Angular") || reload.equals("angular") || reload.equals("ANGULAR")){
            return factory -> {
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
                factory.addErrorPages(error404Page);
            };
        }else {
            return factory -> {
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
                factory.addErrorPages(error404Page);
            };
        }
    }









}
