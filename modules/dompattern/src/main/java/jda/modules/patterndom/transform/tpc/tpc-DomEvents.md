Transformation procedure for the Aggregates pattern <!-- omit in toc -->
=====

# Input <!-- omit in toc -->
## Input model <!-- omit in toc -->
1. a set of domain classes defined in some detail
2. markers of which classes are publisher, which classes are subscribers
## Other input <!-- omit in toc -->
(see pmap-events.json)

# Output <!-- omit in toc -->
- each class is transformed with Publisher/Subscriber design

# 1. Transform publisher class
1. Update class declaration: `implements Publisher`
2. Add-copy attribute: `evtSrc`
3. Update body of each mutator method:
   1. declare and initialise variable `oldState` at the top
   2. add invocation `notify` passing `oldState` as argument
4. Add-copy method `getEventSource`
