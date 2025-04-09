plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    sourceSets {
        main {
            java.srcDir("src")
        }
        test {
            java.srcDir("test")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.testng)
    testImplementation(libs.slf4j.simple)
}

tasks.test {
    useTestNG()
}
