**Design Intention Driven Programming** is a novel software development approach that I proposed and developed as the topic of my MSc thesis at the Open University (UK).

This tool-supported methodology aims to reduce long-term maintenance costs in large-scale software projects.

It encourages the recording of requirements, design intentions, and rationale via structured, object-oriented, mandatory documentation constructs ("comments on steroids") embedded directly in software source code, and provides mechanisms for enforcing adherence to documentation policies.

The scheme can be used to encourage a requirements-driven and fully documented design of the application architecture for a greenfield system, or it can support the systematic understanding and reverse engineering of existing legacy code bases.

The object-oriented commenting scheme is particularly suitable for documenting instances of design patterns and so-called "delocalized plans", or code implementing a feature or requirement that is spread across multiple classes and source code files.

As a proof-of-concept of the approach, I designed an extension to the grammar of Java 5, called **Java with Intentions**, and implemented a prototype of a language pre-processor that could be run during the build step of a CI/CD pipeline.

Technologies: Java, ANTLR v3
