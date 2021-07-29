# JDA Module System

Package `jda.modules` of `jda-main` together with the independent module projects (those defined with the name prefix "`module-`"), implement software modules that extend the core functionality of the MOSA architecture (implemented in the package `jda.mosa`).

Conceptually, a **software module** is a micro-MVC software created by MOSA, consisting of three components (M, V, C). Some modules may contain all three components, others contain only the M and C and not the V. For the later modules, the C is used to handle event handling between model objects and with the calling program.

Each software module can be one of the following three general forms: *independent module*, *embedded module*, *hybrid module*.

## 1. Independent module 
The module's functionality can be executed independently from `jda-main`'s  functionality. This does not mean the functionality is not important, it simply means that we can factor it out from `jda-main` so that we can keep this code base as compact as possible. An independent module is defined in a separate Maven project, whose artifact's name has the prefix "`module-`". 

Examples of very important independent modules are `jda-common` and `module-dcsl`. Examples of add-on independent modules are `module-domainpattern`, `module-dcsltool`, etc.

## 2. Embedded module
The module's functionality needs to be executed as part of `jda-main`. A module is kept running in this form either because its functionality is an integral part of `jda-main` or, for performance or security reasons, it needs to be kept as part of it.

Examples of embedded modules are `dodm`, `setup`, and `ds`.

## 3. Hybrid module
This is a combination of the other two module forms. In this form, some parts of the module's functionality are kept in the embedded mode, while other parts are kept in the the independent mode. 

Examples of hybrid modules are `module-mccl` and `module-sccl`.

## How to choose which module form?
If you have a major piece of functionality that you would like to implement in a module, think about which module form you are going to use. Choices should be made in the following order: 

1. **independent module**: this should be the preferred choice, because this helps keep the module's code away from `jda-main` and thus keeps it compact.
2. **hybrid module**: this should be the next option to consider if independent module is not suitable. Every attempt must be made to minimise the embedded module code parts.
3. **embedded module**: this should be the last option and only applicable if the functionality is a core functionality and must be kept as part of `jda-main`



