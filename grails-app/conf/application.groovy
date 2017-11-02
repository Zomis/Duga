
info {
    app {
        name = '@info.app.name@'
        version = '@info.app.version@'
        grailsVersion = '@info.app.grailsVersion@'
    }
}

spring.groovy.template['check-template-location'] = false

hibernate {
//    naming_strategy = 'org.hibernate.cfg.DefaultNamingStrategy'
    cache {
//        use_second_level_cache = false
        queries = false
    }
}

grails {
    profile = 'web'
    codegen.defaultPackage = 'net.zomis.duga'
    mime {
        types = [ // the first one is the default format
                  all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
                  atom         : 'application/atom+xml',
                  css          : 'text/css',
                  csv          : 'text/csv',
                  form         : 'application/x-www-form-urlencoded',
                  html         : ['text/html', 'application/xhtml+xml'],
                  js           : 'text/javascript',
                  json         : ['application/json', 'text/json'],
                  multipartForm: 'multipart/form-data',
                  rss          : 'application/rss+xml',
                  text         : 'text/plain',
                  hal          : ['application/hal+json', 'application/hal+xml'],
                  xml          : ['text/xml', 'application/xml']
        ]
        disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
    }

    urlmapping.cache.maxsize = 1000
    controllers.defaultScope = 'singleton'
    converters.encoding = 'UTF-8'
    views.default.codec = "html"
    views {
//        'default' { codec = 'html' }
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml'
            codecs {
                expression = 'html'
                scriptlet = 'html'
                scriptlets = 'html'
                taglib = 'none'
                staticparts = 'none'
            }
        }
    }
}
/*
dataSource {
    pooled = true
    jmxExport = true
    driverClassName = 'org.postgresql.Driver'
    // username and password should be specified in duga.groovy
}

environments {
    development {
        dataSource {
            dbCreate = 'update'
            url = 'jdbc:postgresql://db:5432/grails'
        }
    }
    production {
        dataSource {
            dbCreate = 'update'
            url = 'jdbc:postgresql://db:5432/grails'
        }
    }
}*/
