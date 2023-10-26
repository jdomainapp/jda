Module `jdafs-tool`
--------------------
[**GitHub issue**](https://github.com/jdomainapp/jda/issues/59)

# Overview
This is the programming-user-friendly version of the SwGen component. It uses the components of SwGen but provides a somewhat simplified programming API so that novice programming users (such as 1st year students) can learn to create software by following the domain-driven approach.

A typical scenario is as follows. The user would focus on defining the domain model (using DCSL) and the tool would take this model as input and generate an fullstack executable software as the output. The idea is that the user would see the domain model realised in the software, test it and improve the domain model. This is repeated until the domain model satisfies the requirements.

`jdafs-tool` is developed as an extension of the existing DomainAppTool component of jda. This component, however, only supports the Java Swing UI. `jdafs-tool` is designed to support web-based and mobile front-ends. It is built on the current fullstack implementation of the module `jda-mosar`.