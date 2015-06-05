package net.zomis.duga

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application)
    }

    @Override
    void setEnvironment(Environment environment) {
        def config = getClass().getClassLoader().getResource('duga.groovy')
        ConfigObject slurper = new ConfigSlurper().parse(config)
        environment.propertySources.addFirst(new MapPropertySource('duga', slurper))
    }
}