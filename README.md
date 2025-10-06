# cc_restapi4tms

REST API for Task Management System. Toy project to play around with Java, Spring Boot, Spring Web, Validator, Jakarta, Jackson.

Implements a simple REST API. No persistence, only in memory while server is up.

## Prerequisites

### ... for running the server

- Any Java version >= 17 should work. Should `java --version` show an older version, use [SDKMAN](https://sdkman.io/install/) for simple local installation of Java on your system.
- [Maven](https://maven.apache.org/install.html)

### ... for interacting with the server

- any web browser for quick testing of GET functionality
- command line tools like `curl`, `posting`, and other similar tools to interact with the server using GET, POST, DELETE.

## Installation & Running

```sh
git clone https://github.com/DrMicrobit/cc_restapi4tms.git
cd cc_restapi4tms
./mvnw spring-boot:run
```

After a second or two (a few more if it is the first time it starts), the server should be up and listen on port 8080. If not, you will see a build fail message with diagnostics. Most often another program is already listening on port 8080.

## API documentation

Once server is running, browse to [http://localhost:8080/](http://localhost:8080/) for a simple intro on how to interact with the server. Links on that help page also allow to perform (very simple) tests.

## Usage of current AI capabilities for SW development and learning new frameworks

AI was used at different stages and at different times for different purposes. "Fully automated" coding (vibe coding) was not tested, just the nowadays typical "web chat interfaces" (and Copilot in VS Code for documentation purposes).

Specifically, I wanted to test:

1. Suitability of Claude Sonnet 4 and Gemini 2.5pro for high level planning. Both models are a bit older, but well known factors nowadays. Plus, they are cheaper to run than their newer brethren.
2. Suitability of the above models (+ChatGPT) to help implementing / fixing problems in as yet little known frameworks.
3. Suitability of VS Code Copilot to help writing method docs.

### Methodology: where was AI involved

In short, 3 major phases:

1. myGenAssist with mostly Claude Sonnet for high level planning and subsequent implementation proposals
2. "transcription by hand" (no copy paste) into code editor, with Copilot switched off, to imcrease learning effect. In this phase I used the "regular" IDE completetion engine when typing, while using Claude, ChatGPT, and Gemini as 'Explainator' ("Erklärbär") for things I hadn't seen so far in Java and especially Spring.
3. The architecture of the mainly AI generated prototype was fleeced for bugs and refactored by hand where AI architecture or code wasn't good practice. AI support in cases where I had questions like *I want to do X, but can't get syntax/semantics right. How?*
4. Once code was "feature complete and tested", I switched on Copilot in VS Code and started adding Javadoc code documentation via Copilot proposals (sometimes edited).

That is, before writing a single line of code, I wanted to dog-food Claude by asking it to generate an implementation, step-by-step, according to its proposal. If during review of the answers I were to find obvious bugs or weirdnesses, I would make the LLM aware of this to get a new version. Once finished, I planned to have the result become "my" first prototype on which to build on, learn, refactor, and fix problems mostly by hand.

I also ended up testing whether Claude 4.5 Sonnet (public web interface) could generate test cases fully automatically.

### Step by step methodology, results, and (opinionated) final thoughts

See file [AIUsage.md](./AIUsage.md).
