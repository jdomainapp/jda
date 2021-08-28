# JDA Module: SCCL Tool

Implements the tool chain for the SCCL language. This includes `SCCGenTool` which automatically generates an SCC from a domain class.

The tools are kept separate from `module-sccl` (which defines the language) in order to reduce dependencies that other projects/modules may have.