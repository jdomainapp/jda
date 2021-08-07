Transformation procedure for the Aggregates pattern <!-- omit in toc -->
=====

# Input <!-- omit in toc -->
## Input model <!-- omit in toc -->
1. a set of domain classes defined in some detail
## Other input <!-- omit in toc -->
(see pmap-aggregates.json)

# Output <!-- omit in toc -->
- the root class is transformed as `RootCls`
- each member class is transformed as `Publisher`
- Class `ClientProg`

# 1. Transform root class
**Root class**: domain class whose objects serve as roots of at least one type of aggregate.

1. Init: copy the pattern model to replace all references to MemberCls1 by the actual domain class in p-mapping
   1. includes also in method bodies 
2. Update class declaration: `implements AGRoot`
3. Add member field
4. Add/update default constructor method
5. Add-copy ALL remaining methods (which include the followings):
   1. `addMember`
   2. `updateOnMemberAdded`
   3. `checkInvariants`
   4. `handleEvent`
   5. `updateOnMember1Added`
   6. `updateOnMember1Changed` 
   7. `updateOnMember1Removed`
   8. `commitUpdate`
   9. `rollbackUpdate`

# 2. Transform Member class
**Member class**: domain class representing a type of member objects.

Apply pattern DomainEvents to each member class, making the Publishers:
1. Update class declaration: `implements Publisher`
2. Add-copy attribute: `evtSrc`
3. Update body of each mutator method:
   1. declare and initialise variable `oldState` at the top
   2. add invocation `notify` passing `oldState` as argument
4. Add-copy method `getEventSource`

# 3. Transform Client program class

1. Init: copy pattern model to replace all references to MemberCls1 by the actual domain class in p-mapping
   1. includes also in method bodies
2. Add-copy method `main`