Transformation procedure for the Many-to-Many normaliser pattern <!-- omit in toc -->
=====

# Input <!-- omit in toc -->
- two domain classes: C1, C2
  - marked with the pattern
- C1.a2 is defined with a Many-Many @DAssoc to C2
- C1.a2 is defined with:
  - name of normaliser class (e.g. C3)
  - symmetrical: boolean

```
@Pattern(name="dddext.manyManyNormaliser")
@MNormaliser(
  associate=C2.class,
  normClsName="C3",
  symmetrical=true
)
class C1 {
}
```

# Output <!-- omit in toc -->
- C3 is created and is used to define aNorm
- C1 is updated with aNorm
- If symmetrical then C2 is updated with a1 and aNorm
- All classes are updated with the relevant essential behaviours

# Instantiate p-model
- rename C1, C2, CNorm. These include:
  - rename all type references to C1 (e.g. Collection<C1>)
  - rename all the references to C1 embedded in method names (e.g. C2.addC1(), CNorm.setC1())
    - all code statements that invoke these methods
  - all references to C1 in annotation elements (e.g. ascName: "C1-m-assoc-C2")
- rename fields a1, a2, aNorm that reference the above classes (to make them easier to read). These include:
  - textual references of a field in strings (e.g. DAttr.name="a1")
  - code statements referencing a field

# Create C3
1. class name: C3
2. attributes:
   1. a1: C1 
      1. optional = false
      2. has 1-M @DAssoc (endType = Many) to C1
     
    ```
    @DAttr(name="a1",type=Type.Domain,optional=false)
    @DAssoc(ascName="C1-C3",role="c3",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
      associate=@Associate(type=C1.class,cardMin=1,cardMax=1),
      dependsOn=true)
    private C1 a1;
    ```
   2. a2: C2
      1. optional = false
      2. has 1-M @DAssoc (endType = Many) to C2
3. add essential behaviour space (uses BSpaceTool)
   1. required constructor
   2. a1: mutator & observer
   3. a2: mutator & observer

# Transform C1
1. Add attribute a2: 
   1. a2: Collection<C2>
   2. has Many-Many @DAssoc to C2
  ```
  @DAttr(name="c2",type=Type.Collection,serialisable=false)
  @DAssoc(ascName="C1-C2",role="c1",
    ascType=AssocType.Many2Many,endType=AssocEndType.Many,
    associate=@Associate(type=C2.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE),
    normAttrib="c3")
  private Collection<C2> c2;
   ```

2. Add attribute aNorm: 
   1. c3: C3
   2. has One-Many @DAssoc (endType = One) to C3
3. add relevant essential behaviours (uses BSpaceTool):
   1. required constructor
   2. a2: mutator, observer, (private) link adder & link remover
   3. c3: link adder & link remover
      1. invokes a2's link adder and link remover (resp.)

# Transform C2
Pre-condition: @Pattern(C1).symmetrical = true

1. Add attribute a1: 
   1. a1: Collection<C1>
   2. has Many-Many @DAssoc to C1
2. Add attribute aNorm: 
   1. c3: C3
   2. has One-Many @DAssoc (endType = One) to C3
3. add relevant essential behaviours (uses BSpaceTool):
   1. required constructor
   2. a1: mutator, observer, (private) link adder & link remover
   3. c3: link adder & link remover
      1. invokes a1's link adder and link remover (resp.)

