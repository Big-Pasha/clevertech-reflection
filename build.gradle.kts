plugins {
    id("java")
}

group = "org.clevertech.reflection.pz"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

val lombokVersion = "1.18.30"
val springBootVersion = "3.3.0"

dependencies {
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")

    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
}

tasks.test {
    useJUnitPlatform()
}