# GATor (Global Ads aggregaTor)

Application to parse different classifieds boards. Easily customizable (just create your own by.homesite.parser implementation)

This this the backed server (http://gator.homesite.by as a demo).

## Development

To start your application in the dev profile, run:

```
./mvnw
```

## Building for production

### Packaging as jar

To build the final jar and optimize the gator application for production, run:

```
./mvnw -Pprod clean verify
```

To ensure everything worked, run:

    java -jar target/*.jar

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./mvnw -Pprod,war clean verify

## Testing

To launch your application's tests, run:

    ./mvnw verify

## setting up as debian daemon

mcedit /etc/systemd/system/gator.service

[Unit]
Description=gator
After=syslog.target

[Service]
User=gator
ExecStart=/mnt/vol1/www/gator/gator.jar
SuccessExitStatus=143
StandardOutput=append:/var/log/gator/output.log
StandardError=append:/var/log/gator/error.log

[Install]
WantedBy=multi-user.target

## Elasticsearch

version 7.1.0 required (run under java 11)

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a mariadb database in a docker container, run:

```
docker-compose -f src/main/docker/mariadb.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f src/main/docker/mariadb.yml down
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```
