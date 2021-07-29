# Package: `jda.mosa`

This package contains the core components that make up the **Module-Based Software Architecture (MOSA)**. MOSA is an MVC architecture. 

Each **module** in the architecture is an MVC module structure. Abstractly, module is defined in the subpackage `module`. A module consists in three components: 
- M: the model and is defined in the subpackage `model`
- V: the view and is defined in the subpackage `view`
- C: the controller and is defined in the subpackage `controller`

A **software** consists of a set of modules. Software is defined in the subpackage named `software`.
