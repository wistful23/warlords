plugins {
    java
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    sourceSets.main {
        java.srcDir("src")
        resources.srcDir("../data")
    }
}

dependencies {
    implementation(project(":common"))
}

application {
    mainClass.set("com.def.warlords.desktop.Main")
}
