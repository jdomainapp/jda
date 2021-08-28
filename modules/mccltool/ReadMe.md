# JDA Module: MCCL Tool

Implements the tool chain for the MCCL language. This includes `MCCGenTool` which automatically generates an MCC from a domain class.

The tools are kept separate from `module-mccl` (which defines the language) in order to reduce dependencies that other projects/modules may have.