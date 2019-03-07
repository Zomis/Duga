class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/stats"(controller: "manualStats", action: "stats")
        "/hook"(controller: "githubHook", action: "hook")
        "/hooks/github/payload"(controller: "githubHook", action: "hook")
        "/hooks/bitbucket"(controller: "bitbucket", action: "bitbucket")
        "/hooks/appveyor"(controller: "appveyorHook", action: "build")
        "/hooks/travis/payload"(controller: "travisHook", action: "build")
        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
