package com.littlecity.server.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

/**
 * @author huangxiaocheng
 * @Date 2019/9/17
 **/
@Sources("classpath:ServerConfig.properties")
public interface ServerConfig extends Config {

    @DefaultValue("dev")
    String env();

    @DefaultValue("8080")
    @Key("server.${env}.port")
    Integer port();

    @DefaultValue("C:\\")
    @Key("server.${env}.fileDir")
    String fileDir();

    @DefaultValue("1024")
    @Key("server.${env}.maxFileSize")
    Integer maxFileSize();

    @DefaultValue("localhost")
    @Key("server.${env}.hostName")
    String hostName();


}
