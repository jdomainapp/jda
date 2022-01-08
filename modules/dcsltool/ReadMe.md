# JDA Module: DCSL Tool

Implements the tool chain for the DCSL language. This includes `BSpaceGen` which automatically generates behaviour space from the state space.

The tools are kept separate from `module-dcsl` (which defines the language) in order to reduce dependencies that other projects/modules may have. All projects depend on `module-dcsl` but not every project needs to use the DCSL tools.