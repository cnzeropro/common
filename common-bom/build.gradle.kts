plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
}

group = "org.zero"
version = "1.0.0"
description = "common-bom"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
