rootProject.name = "RedEngDev.BranchingNode-1.2.0"

plugins {
    // See documentation on https://scaffoldit.dev
    id("dev.scaffoldit") version "0.2.+"
}

// Would you like to do a split project?
// Create a folder named "common", then configure details with `common { }`

hytale {
    usePatchline("release")
    useVersion("latest")

    repositories {
        // Any external repositories besides: MavenLocal, MavenCentral, HytaleMaven, and CurseMaven
    }

    dependencies {
        // Any external dependency you also want to include
        implementation("curse.maven:renode-1531866:8180973")
    }

    manifest {
        Group = "RedEngDev"
        Name = "BranchingNode"
        Main = "dev.redengdev.BranchingNodePlugin"
        OptionalDependencies = mapOf(Pair("Verday:Renode", "0.8.0"))
    }
}