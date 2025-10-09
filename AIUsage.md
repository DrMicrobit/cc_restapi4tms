
# Learning Log: current AI capabilities for SW development and learning new frameworks

AI was used at different stages and at different times for different purposes. "Fully automated" coding (vibe coding) was not tested, just the nowadays typical "web chat interfaces" (and Copilot in VS Code).

## Test goals

### Planned

Specifically, I wanted to test:

1. Suitability of Claude Sonnet 4 and Gemini 2.5pro for high level planning. Both models are a bit older, but well known factors nowadays. Plus, they are cheaper to run than their newer brethren.
2. Suitability of the above models (+ChatGPT) to help implementing / fixing problems in as yet little known frameworks.
3. Suitability of VS Code Copilot to help writing method docs.

### Unplanned

- On a whim, as there was still time left, I wanted to try Claude Sonnet 4.5 via public web interface and see whether it could generate tests fully automatically.
- after "finalising" the main trunk of this repo, I stumbled across a bug. As this happened while I was exploring Cline with a small local model, I tested Qwen 3 coder 30B Q4 on this bug.

## Methodology: where was AI involved

In short, 4 major phases:

1. myGenAssist with mostly Claude Sonnet for high level planning and subsequent implementation proposals
2. "transcription by hand" (no copy paste) into code editor, with Copilot switched off, to imcrease learning effect. In this phase I used the "regular" IDE completetion engine when typing, while using Claude, ChatGPT, and Gemini as 'Explainator' ("Erklärbär") for things I hadn't seen so far in Java and especially Spring.
3. The architecture of the mainly AI generated prototype was fleeced for bugs and refactored by hand where AI architecture or code wasn't good practice. AI support in cases where I had questions like *I want to do X, but can't get syntax/semantics right. How?*
4. Once code was "feature complete and tested", I switched on Copilot in VS Code and started adding Javadoc code documentation via Copilot proposals (sometimes edited).

That is, before writing a single line of code, I wanted to dog-food Claude by asking it to generate an implementation, step-by-step, according to its proposal. If during review of the answers I were to find obvious bugs or weirdnesses, I would make the LLM aware of this to get a new version. Once finished, I planned to have the result become "my" first prototype on which to build on, learn, refactor, and fix problems mostly by hand.

I also ended up testing whether Claude 4.5 Sonnet (public web interface) could generate test cases fully automatically.

### Pre-coding: architecture and high level components

1. A myGenAssist Assistant was set-up. System message along the lines of
`User is experienced developer in C++ and Python, but comparatively new to Java. Follow the directions of the user requests.
The following are the instructions for a challenge: ...
`
Text from challenge was added almost verbatim, with few edits / deletions.
2. The assistant was run in two separate sessions with two different models, once as Gemini 2.5pro (high reasoning), once as Claude 4 Sonnet (high reasoning). The prompt for both runs was along of *"How would you propose to tackle the challenge?"*
3. Initial AI responses were evaluated (see results). General approach of Claude was deemed superior and chosen for further continuation. The Gemini 2.5pro approach was not continued.
4. A second myGenAssist was set-up. Claude 4 Sonnet, high reasoning. Prompt as above, but a slightly redacted version of the Claude proposal from step 2 was added as background information, together with a comment along the lines of "phase 1 of the proposed setup via initializr has been executed and is done."
5. A new chat was opened. For each phase of the high-level planning:  second assistant was then asked to work "on phase X". Each sub-level proposal was briefly evaluated and/or commented for obvious errors before the assistant was asked to work on the next phase.

This concluded the pre-coding phase.

### Coding

Coding environments (testing two different Java versions):

- Ubuntu 24.04 with Java 21 (SDKMAN Temurin) and Maven 3.8.7
- OSX 26.0.1 with Java 17 (SDKMAN Temurin) and Maven 3.9.11
- VSCode with Language Support for Java(TM) by Red Hat

#### First phase: Prototype that runs

1. GitHub project was set-up
2. A [http://start.spring.io](http://start.spring.io) project was set-up: Java 17, Maven, Spring Boot 3.5.6. Dependencies: Spring Web, Validation. Downloaded, tested for run-ability and added to git.
3. Personal coding environment settings for VS Code added. (language support, auto-format settings). GitHub Co-Pilot was switched off.
4. New branch "dev", from there new branch "dev_mvp". Early decisions
   1. to NOT use the [CheckerFramework](https://checkerframework.org/) for this project. Separation of concerns: mixing a slightly fickle checker framework with totally new framework (Jakarta, Jackson) and quite new (Spring Boot, which has an own checking) seemed like a bad idea.
   2. to not have a user story or requirements document beside challenge instructions and results from Claude pre-coding exploration. Run development of Challenge code as exploration and learning of capabilities and behaviour of unknown frameworks.
5. Proposals generated by Claude in pre-coding step 5 were typed in by hand, correcting some first obvious errors, adding code comments on what to think of / work on once prototype MVP runs (both in code files and DEVLOG.md). From time to time, LLMs (Claude or OpenAI) were asked to explain concepts new to me. Git commits initially once a version of a file / layer compiled without errors.
6. Once a prototype ran and answered to GET /tasks, performed a merge --squash of dev_mvp back into dev. This version ran, but was known to have bugs and problems.

#### Second phase: fix and learn

7. Switch from dev to new branch dev_fixandlearn. Decision at this point to forego testing framework (scheduled to: if time permits) and use `curl` / `posting` for interactive exploration. Two reasons:
   1. exploration of behaviour of so far unknown jakarta / jackson framework easier when interactive and
   2. challenge instructions (and challenge is simple enough)
8. While working on and testing requirements given in challenge instructions (roughly in order GET, POST, DELETE, GET again): worked through all commented TODOs and thoughts gathered during prototype creation. Cleaned and simplified architecture and code.
9. Added some last touches for online documentation
10. Compiled this document.

## Results including discussion

### Results pre-coding: phase high-level approach

Initial responses by Claude and Gemini to question "How do you propose the challenge should be tackled" very, very different.

#### Claude high level approach

Claude had well structured planning in 5 sections, but I missed warning signs for later goofs:

1. Tech Stack. Good, short, just Spring Boot
2. Project Structure. Short and sweet: displayed source tree with minimal comments, clearly delineating architecture setup of the API with 4 modules (model, repository, service, controller). I had been thinking along a MVP setup (model, view, presenter), the split of M into model and repository though seemed reasonable. And naming turned out to be more Spring conforming.
3. Implementation steps:
   1. Setup. Gave quick overview what to expect: Spring Initializr, dependencies, setup of basic project structure. Matched my expectation (minus dependencies, unknown to ma at that time).
   2. Core model for Task. Here I did not recognize early warning sign that Claude would goof by adding validation annotations here. Also: early warning sign it would mix business logic into this layer with UUID generation.
   3. Storage Layer. Clear plan for in-memory repository (named ConcurrentHashMap) with CRUD operations and initial population of pre-defined tasks as per challenge. Matched my expectations.
   4. REST controller. Looked good: implementing GET and POST from challenge, together with basic validation and error handling. Matched my expectations.
   5. Implementation of enhancement. Matched my expectation.
4. Key Java/Spring concepts that would be learned. In retrospect: weaker part. Just named a few key words (e.g. @RestController, @RequestMapping, etc.) without explanation. Could not judge.
5. Development Workflow. Short, ok-ish.

#### Gemini high level approach

Gemini's answer was much messier than Claude. it had no clean high-level overview at the start, mixed the overview already with technical specifications and implementation details. The 6 steps proposed were:

1. Project initialisation. 3 sub-steps, steps 2 and 3 were to far into implementation
   1. Project initialisation. Also used Spring Initializr (good), with correct group and artefacts (which Claude would mess up later, more on that below). Dependencies: Spring Web (good), Lombok (know what it does, but did not want yet another new framework for me), Spring Boot Dev Tools (optional)
   2. Downloading ...
   3. ... and first run of the project.
2. Coding the Task model. Waaaay too far into implementation, including specific types and code fragments. Plus points though for suggesting java.time.Instant (Claude would goof and use LocalDateTime)
3. Coding In-Memory Storage. As above: too much implementation, but not enough to get coherent picture.
4. Coding Service Layer. As above.
5. Coding Controller. As above.
6. Data seeding and validation. As above.

#### Choice taken: Claude

Easy decision: approach of Claude.

### Results pre-coding phase "Claude, guide through your steps."

Not bad, sometimes even good. In retrospect (after having implemented everything and done some refactoring), Claude committed a number of goofs and/or took debatable decisions. Details below.

Claude (and other LLMs btw) seems to like functional programming, especially via stream(), as canonical way of Java doing things. OK with me as long as we are not talking high-performance computing. Clearly not the case here.

#### Pre-coding: Project Setup

This step was execute with same chat (context) as initial high-leval approach above. Results looked good on visual inspection, with one small goof in naming the Maven group id.

1. Spring Initializr. OK-ish. recognized that Claude goofed with the Maven group ("com.taskapi" instead of reserved "com.example")
2. Dependencies to choose Initilizr. Good: Spring Web and Validation.
3. ... Steps 3 to 6 looked all good: download, extract, verifying `pom.xml`, testing via `./mvnw spring-boot:run`, and test connection to localhost:8080
7. Creating package structure. Good.
8. VSCode extensions. Good. Proposed extension pack for Java and Spring Boot extension pack.

All in all, I was happy with how Claude seemed to operate.

#### Pre-coding: Implementation

##### model/Task.java

With the same chat context as above, kickstarted via "Now show step 2.1 please, the entity."
The answer gave clear directions which file to create (model/Task.java) and showed the whole file in one go. Thereafter, it explained some key concepts (validation @NotBlank, @Pattern, etc.)as well as the choice of LocalDateTime being more modern that "Timestamp."

For someone having little experience with `jakarta.validation.*` and the evolving Java time interface, this looked plausible.

Points of contention:

- debatable: used class instead of record. Maybe explainable in case fast and memory efficient updates are planned, but this would opens another can of worms (no defense against inadvertant changes)
- debatable: store "status" as String, not as enum.
- debatable: have Task update "updatedAt" when setters are called.
- goof: define a public constructor without any parameters. Together with way how variables were declared, could lead to objects with null values.
- goof: have Task generate its own UUID. Belongs to business layer. Or at least to TaskRepository to handle possible duplicate UUIDs.
- goof: using LocalDateTime instead of ZonedDateTime. Even bigger goof as apparently java.time.Instant should have been used.
- major goof: jakarta validation annotations in this file. As I learned later: annotations themself do not take effect on their own in constructors & setters.

##### model/TaskRepository.java (and TaskRepositoryInMemory.java)

In general, proposal was pretty solid, with little to criticise.

Point of contention:

- Missed opportunity: Claude only produced TaskRepository.java as fully fleshed out class. It did not abstract intially into an interface as to, e.g., be able to easily inject later other implementations or for mock testing. Asking Claude to design this as interface produced the expected result.

Interesting:

- Claude generated by itself .isEmpty() method, though I haven't seen this in as part of any Spring Repository (CRUD, Jpa, etc.) interface.
- Claude combined functions of different repository types, at least CRUDRepository and JpaRepository

My goof: I asked Claude to "guide through Phase 3.1, creation of the in-memory repository". As Claude also directly implemented 3.2 (CRUD operations), I did not catch it had stopped short of its phase 3.3 (add pre-defined tasks during startup). I also did not catch that this, in the end, does not belong to the repository, but to the service layer.

##### model/TaskService.java

Proposal from Claude OK with slight oversights:

- Exceptions (realised only during coding): Claude directly throws exceptions geared towards HTTP response. Breaking separation of concerns, as this couples TaskService tightly to specific REST controller.
- debatable: checking a string for multiple values of 'valid' was realised via if-elseif... string comparison cascade. Starting with 3 values, I prefer looking up values in a set --> bundles the valid strings into one place in a file.

##### model/TaskController.java

Proposal from Claude again OK-ish. Like for model/Task, Claude preferred a class for the data transfer object (DTO) instead of a record. This was a very clear goof.

### Results from coding phase: LLMs as "Explainators"

- I feel that Claude, Gemini, and ChatGPT are pretty good as Explainators ("Erklärbären")
- It helps to have assistants in myGenAssist which can be pre-configured with a SYSTEM message to set the context.
- In the past I preferred Gemini 2.5pro in this role, but ChatGPT 5 (also tested via public webinterface without assistant) seems to have caught up.

### Results from coding phase: Copilot as assistant for generating source documentation

Quite efficient. After one or two examples, Copilot picks up style and intents of comments. It often makes proposals that can be one-tabbed (completed) and be done with it. Sometimes proposals are wrong, but that is easily fixable.

Big time saver.

### Results from coding phase: Claude 4.5 Sonnet for automatic test case generation

> This was a triumph. I'm making a note here: HUGE SUCCESS. It's hard to overstate my satisfaction.
>
> -- GLaDOS

Claude was given only controller/TaskController.java as input. It generated a first test file that tested the web layer only. Ran perfectly (and no errors). Claude was then asked to test the whole application and generated a full integration test.

The full integration test unearthed an error where logs hinted at time handling. Given the error message, CLaude immediately pointed to ZonedDateTime and conflicts with Instant.

The fix was easy enough, even without AI, and then the integration tests ran through.

Code generated by Claude 4.5 Sonnet was near perfect, just a few warnings in the IDE. Additionally, Claude missed to generate a certain test case (duplicate author/title) for the integration test, but as the code / comments for the controller hinted at the possibility but did not specify when, this is understandable.

> This is fine.
>
> -- Question Hound Dog

Of course, it's not. The 'fix' introduced a regression in the sense that the time serialised as JSON included fractional seconds. Claude's test tid not check for that ... and I had scheduled "learn to write tests" for later, i.e., I did not check Claude's code to make.

After a detour via Cline/Qwen 3 coder local which failed (see below), I reverted back to Claude 4.5 to get a fix for both the bug and the integration test bug.

### Results from coding phase: Qwen 3 Coder 30B (Q4, 48k context)

After "finalising" main, I was in the process of testing Cline with a local model to see the status quo of pseudo-agentic development tools with small models, see also [AMD's blog on the Cline website](https://cline.bot/blog/local-models-amd) on their take regarding local models. I chose to experiment with Qwen 3 Coder 30B (Q4) which I could push to 48k context. I used it to  explore integrating Swagger/OpenAPI. During these tests I stumbled across the bug (and the fact the integration test did not catch it).

I asked Cline/Qwen to first change the integration test in such a way it would catch the error. In short: my initial attempts failed, no "solution" proposed caught the error.

### Side note: Copilot as assistant for coding

This wasn't used here, but I had tested Copilot late in my personal Java 1brc challenge in coding mode and already thought that it helped by sometimes proposing function code snippets that one hadn't even thought of. In case these solutions are idiomatic this helps re-inforcement learning on how to do thigs right.

Sometimes though the proposals are silly and / or distracting. Double-edged sword it is.

## Final thoughts (opinionated): LLMs for SW development and learning

In short ... yes please, but especially for learning: in moderation! And, atm, never in a hurry and without checking what actually happens.

Pro:

- can save time both in preparatory steps as well as during coding or documentation
- solutions proposed range mostly from ok-ish to pretty good (with a few duds here and there)
- also pretty good for a first exploration of unknown territory like, e.g., when picking up a new language framework. A simple query like `<explanation of context> What are current best practices to tackle this problem?` can give insights quite fast and opens up avenues for further investigation.
- also pretty good for questions like `what does this code do?`
- i.e., decreases time until being productive

Con (with cloud models)

- code is sent to big companies and corporations ... what could possibly go wrong?

Con (for learning)

- I still think a well structured and written introductory book or course is way better than only relying on LLMS. I see both complementary.
- Don't copy & paste (or one-tab in the case of Copilot)! One learns exactly nothing doing this. Typing helps memorising (syntax/semantics) as one has time to analyse code and architecture "in the background tasks of your brain" while possibly thinking about alternatives.
- one *will* be tempted to simple copy/paste and be done with it because that is so fast (and there are always more impotant things to get done). You. Have. Been. Warned. 

Con (for development)

- the *LLM as architect* is not bad, but like for coding, it needs (sometimes very tight) oversight. This explains the current trend I observe in agentic AI systems for software development that still keep developers in the loop, sometimes asking them to read through pages and pages of specifications after each step.
- AI will goof, sometimes blatantly so. When it happens in areas where one is very new to, this can lead to absolutely horrendous 'solutions' being implemented. In the best case this leads to code not working as specified, and one can then use that as personal learning experience for getting to know the field better and finding better solutions. In the worst case the code works for small test cases, but bugs like abysmal run-time for real world data sets or memory leaks appear in production.
