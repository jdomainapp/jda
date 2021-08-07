Transformation procedure for the Aggregates pattern <!-- omit in toc -->
=====

# Input <!-- omit in toc -->
## Input model <!-- omit in toc -->
1. a set of domain classes defined in detail (including the associations) in DCSL
## Other input <!-- omit in toc -->
1. Root class marker
2. one or more aggregate specifications, each specification consists in:
  - name: String
  - boundary: array of member classes
  - constraint classes: array

```
  @Pattern(name="dddcore.aggregates")
  @Aggregates(
    root=RootClass.class,
    aggregates = {
      @ag(
      name="Aggregate2",
      boundary={MemberClsX.class,...},
      constraints={ConstraintX.class,...}),
      @ag(
      name="Aggregate2",
      boundary={MemberClsY.class,...},
      constraints={ConstraintY.class,...})      
    }
  )
  class RootClass {...}
```
# Output <!-- omit in toc -->
- the root class is transformed as `RootCls`
- each member class is transformed as `Publisher`
- Aggregate classes: one for each aggregate spec
- Class `ClientProg`

# 1. Transform Aggregate classes
1. Create a class `Aggregate_i` as a subtype of `Aggregate` for each aggregate `i` of the `RootCls`

# 2. Transform root class
**Root class**: domain class whose objects serve as roots of at least one type of aggregate.

1. Update class declaration: `implements AGRoot`
2. Add attribute `private Map<String, Aggregate> ags`
3. Update constructor's body: 
   1. add invocation `createAggregate()`
4. Add method `addMember`
   1. throws `ConstraintViolationException`
   2. invoke `updateOnMemberAdded`
5. Add method `updateOnMemberAdded`:
   1. add this as subscriber of `member`
    ```
    EventType[] evtTypes = CMEventType.values();
    member.addSubscriber(this, evtTypes);
    ```
   2. create one "linked member block" for each member class associated to root class:
      1. add `try...catch (ConstraintViolationException)` around the invocation to `updateOnMemberAdded`
6. for each member class `C` in all aggregates:
   1. Add a method `updateOnCAdded`
      1. throws `ConstraintViolationException`
      2. declare and initialise variable `oldState` at the top
      3. add invocation to `commitUpdate`, passing `oldState` as argument
   2. Add method `updateOnCChanged` 
      1. throws `ConstraintViolationException`
      2. declare and initialise variable `oldState` at the top
      3. add invocation to `commitUpdate`, passing `oldState` as argument
   3. Add method `updateOnCRemoved`
      1. throws `ConstraintViolationException`
      2. declare and initialise variable `oldState` at the top
      3. add invocation to `commitUpdate`, passing `oldState` as argument
7. Add method `commitUpdate`
  - could need one method for each member class `C`
7. Add method `rollbackUpdate`
  - could need one method for each member class `C`
8. Add method `createAggregate`
   1.  for each aggregate `i` of `RootCls`, create an aggregate instance of the class `Aggregate_i`
       1.  boundary: an array of domain classes representing the member objects
       2.  constraints: an implementation of the interface `Constraint` for each type of constraints needed. 
   2.  add aggregate instance to `ags`
9.  Add method `checkInvariants`
10. Add method `handleEvent`
    1. create one invocation to `updateOnC...` method for each member class `C` of the event's source object

# 3. Transform Member class
**Member class**: domain class representing a type of member objects.

Apply pattern DomainEvents to each member class:
1. Update class declaration: `implements Publisher`
2. Add static attribute: `evtSrc`
3. Update body of each mutator method:
   1. declare and initialise variable `oldState` at the top
   2. add invocation `notifyChangeChanged` passing `oldState` as argument
4. Add method `getEventSource`

# 4. Transform Client program class

1. Create class ClientProg
2. create a root object
3. add member objects through root
4. add `try...catch(ConstraintViolationException)` around each invocation to a mutator method of the root object