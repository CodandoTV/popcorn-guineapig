# Contributions 🤝

We encourage contributions of all types! Whether it's reporting issues, suggesting new features, or submitting pull requests, you're welcome to help improve the plugin.

- Check out the [issues](https://github.com/CodandoTV/popcorn-guineapig/issues) page for ideas.
- Feel free to submit [pull requests](https://github.com/CodandoTV/popcorn-guineapig/pulls).

## How to create the first contribution? 📚

### 1. Fork the repository

Create a fork of the repository in your own GitHub account.

### 2. Clone the repository

```bash
git clone https://github.com/your-user/your-fork
```

### 3. Access the repository

```bash
cd your-fork
```

### 4. Make your changes

- Update the code.

### 5. Commit the changes to your fork

```bash
git checkout -b "your-branch-name"
git add .
git commit -m "Describe your changes"
git push
```

### 6. Create your Pull Request

- Create a new Pull Request, selecting the original repository as the base and your branch as the compare branch

- Provide a clear title and a detailed description of your changes

- Submit the PR and wait for review and possible approval.

## How to test the popcorngp locally in a project?

### Publishing the library locally

!!!warning "Use JVM 17"
    Make sure your Gradle is using **JVM 17**, otherwise you could have some issues.

To run the plugin locally you need to publish it in your local machine using `mavenLocal`. 
You can follow the steps:

1- Update the `popcornguineapigplugin/build.gradle.kts` commenting the lines:

```kotlin
...
mavenPublishing {
    //publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    //signAllPublications()
    ...
```

2- Publish the plugin locally, using the command:

```shell
./gradlew :popcornguineapigplugin:publishToMavenLocal
```

After that, the plugin artifacts will be in your machine in the following path 
`$HOME/.m2/repository/io/github/codandotv/popcornguineapig/x.x.x`.

### Using the local library in any project

1- In your project, you can use by adding the `mavenLocal()` repository in the following places:

```kotlin
// settings.gradle.kts
pluginManagement {
    ...
    repositories {
        ...
        mavenLocal()
    }
}
```

```kotlin
// build-logic/build.gradle.kts
...
repositories {
    mavenLocal()
    // ...
}

dependencies {
    implementation("io.github.codandotv:popcornguineapig:x.x.x")
}
```

## How to run the documentation locally?

- Create your virtual env:

```shell
python3 -m venv venv
```

- Open the new env:

```shell
source venv/bin/activate
```

- Install mkdocs-material:

```shell
pip install mkdocs-material
```

- Start the local server:

```shell
mkdocs serve --watch .
```

Access your documentation at `http://127.0.0.1:8000/`

## Some tips

- We primarily use IntelliJ IDEA for developing new features and fixing bugs. While it's possible to use Android Studio, IntelliJ IDEA is the more convenient option, especially since we are working on a Gradle plugin;

- When developing a **new architecture rule**, make sure to **write unit tests** for it. Additionally, you can create a sample in the sample directory to simulate the architecture violation;

- If you have any questions about contributions, feel free to reach out to someone from CodandoTV. You can contact [Gabriel Moro](mailto:gabrielbronzattimoro.es@gmail.com) or [Rodrigo Vianna](mailto:rodrigo.vianna.oliveira@gmail.com).

🤗 Happy coding!