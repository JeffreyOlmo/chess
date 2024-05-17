# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

Endpoint diagram link: https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=C4S2BsFMAIAUAsCGBnGAmAUBgDogTqAMYi4B2w0AwuCJOTvkSYudAMqR4BunDBIxMhQASLACZQ8fJkPacuAyNIHNWAEUTBEAQUKFIyZMsEsKGrQCMUSjNVrkAtAD4O3TgC5owgCrfY0NQBRABlA70DoAHoxCwxXHjxnUVIJD2gAJQB7AFdgGDxIAEdsgwpgTOgkFMkMZNTEl3lFT0pEcHBoQih8aFRuRTim-WdzHT0DZBbuvGg2jrFNRAxR3X1DEcWrVE81SCg82fbobL7kABpoAHNEAFsDC-FZ3PhocoBrOiNRrcgHDa1VhNPLo3qRMgB3KBiS53ViZABm0DEe0goEypGWi0B60a-X0nnSqOyeFIvWy40MgzxvycdUkBKJJLJFOQvS0wBOtXEkmc8TSHBSXl8-jQAAZRdACshsOjUFSEn8nHY6MBPD4-NAxaKsLh+CZWMr6LqZKY5G4pMaVLI6bxLfqKPEBnbVGYsSzjC6AptrFhDcBefI0ur-LAAPJsbxRVCGEDo6AAbxOnFIt0gF1whnBmTwYgAvvLOEluWldn0QG0QAAvfJFErIYBc6qF3EKfFUAqaGDgTKXECkgrFUryxT-MZrSYZVF4Wg8Y59aAWACec+TqcxAJZo5+ngAiiU8Muk1JvtZFSsWQyOUyj0jFuuxxMA9TL8TSTeFlph22AGqcEDw5cM2QLMcy-Glz3HFoOwORBnleTIPgxCDHycE9tnYcoCieYAXneOh7x+M83Ug6AQTBSFIGhGB62zRBLiUZCcUdNtCSvUlYJw+DELAosmzwF8mQ43CEPwm0Gj5fizXLGhq0lAwZVIOUJMVP01WFTVxQTI8UzuB5nm8ETSHzD1ZD9EzTQk8zWDEqyHSGJRnVkRijEc000JsP0nwSNSNSCUJwijCZY1JeMhIMxD82U2li0krJchrQd63gyoYsbeovOaaBvwrD8YCErj8OY8DiKBSdgGnSBZ3yvCkJKnF3N3fdl2qwyCNPUdsQnVjX2w4TENvT9nIyljGXYuCaoGpYio6i8AhRGDxtaobUO9dDCRuTIqsW7j3KIjcSLIiEoXopF5uC+9OuGyABNJZByXHHjor4m7mXHNlNE5MSvP5OgxCFDUtTk6VZSUZTnFU-6RXFHVGCtU0zNc1hLMRkRUpRs1Wwc2H7S9faJls3HEB+X0aBVb7JODaAAHEwiia47gTMLDMiwMGjEgkcgOAc6zKCoqnqNKeRbTLspoXLeoKjFppWvHDEvCqts4mqLs3GWiesRrOGa7b8N2maSO6wSddJD8lmWoqXpa-rTce5z5ZnPKjnpgwVfHLcNegPctcODpna+Vbfn10rDdJP3JtdlCLbKnqw5t6X2ejpkaCShErlTIwvpbH7pwrWSpQUpTWZU0nyB8qGJXjP3PAAbWdyaAF1jPRhHsc9ZHW+tNGO4s+yCecvuA5J+x-Szin1LDCM6dTRn9MMi5nYAOVTFnzV4+oOfiuTEt5lK+MF5so9FkBxatwr7KDuWyoVvLjYj+qA81g8JeVvW1c6y3jfD837I-pXDPD6Wdt2yQE7NAUgkBwRpzuHfZA7t0JsEwjAOuNtX5AMOhRKibJaL0RgVdF6dcACSahHoJxDlAmARD97iVZp4Vw0kqw1mBopUGRdwYl1VJDDSFdnZEKbt3A07CCbtz1J6Gy6MioD1li5fhrpLA+lsOw8mZc4AAFVIyRDrqFWeiF0zgEQIuTglBMjdjwPPVMvCCxsxihvLmtZSjJX5jUTOh8cqgNPlLc+b9ZqsWvs-JadVYFq23J7JqfidoBz2g+S+ZD3FfwCXgxOY0-7WzvIAgJ9tKpIOnkuaAlDloNRCd7ZBd5UHpMSeQuJUiElkOKZ+KOlB4CQEIG8aA-4t480op0Yx2ZWmskQFwRAIA9EWCgLbMpKjsDizruCMALxsB6IMTMQg3TjzxKCR7CZUzp4oIiRfCc6DjowGyJMzsuDhYjTYq9fG8drHlLuiyd6HIM4xSUVJXOjCC4sNXmw4eyitRYCAA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
