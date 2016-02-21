How to run Duga on Docker
=========================

Clone the duga repository

Edit `grails-app/conf/duga_example.groovy` to set your settings. Leave the dataSource configuration as-is. Also rename this to `duga.groovy`

Run `./gradlew war`

Create the directory `docker/webapps` and copy `build/libs/*.war` to `docker/webapps/*.war`

Inside the `docker` directory, run `docker-compose up`
