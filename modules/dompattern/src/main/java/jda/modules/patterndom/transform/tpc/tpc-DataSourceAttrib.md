Transformation procedure for the DataSourceAttribute pattern <!-- omit in toc -->
=====

# Input <!-- omit in toc -->
- Two domain classes: C1, C2
- Class C1 is marked with:
  - aSrc name
  - aBound name
  - C2.aBoundFilter name
  - filter query 

```
@DataSourceAttribute(
  srcAttr="aSrc",
  boundAttr="aBound",
  query=@QueryDef(
    clazz=C2
    exps={
      @AttrExp({"aBoundFilter", "op", "val"})
    }
  )
)
class C1 {
  //
}

class C2 {
  @DAttr(...)
  private Object aBoundFilter;
}
```

# Output <!-- omit in toc -->
- C1 is updated with suitable elements of the pattern
- C2 is unchanged
  
# Transform C1
1. Add attribute aSrc:  
   1. add field declaration
   2. add @DAttr(...)
   3. copy @QueryDef(...) from C1 to aSrc
  ```
  @DAttr(type=Collections, serialisable=false, virtual=true, sourceQuery=true) 
  @QueryDef(
    clazz=C2
    exps={
      @AttributeExp (attrib="aBoundFilter", op="op", value="val"})
    })
  private Collection<C2> aSrc;
  ```
2. Add attribute aBound: 
  ``` 
  @DAttr(type=Domain, sourceAttribute="aSrc") 
  private C2 aBound;
  ```
3. Add getter, setter methods for aSrc
4. Add getter, setter methods for aBound

