Transformation procedure for the MultiSourceDerivedAttribute pattern <!-- omit in toc -->
=====

# Input <!-- omit in toc -->
- a domain class C
- source attribute names

```
@Pattern(name="dddext.multiSourceDerivedAttribute")
@MultiSourceDerivedAttribute{
  aDerived="aDerived",
  srcAttribs={"a1", "a2"}
}
class C {
  @DAttr(...)
  private Object a1;
  @DAttr(...)
  private Object a2;

  private Object aDerived;
}
```

# Output <!-- omit in toc -->
- C is updated with suitable members of the pattern

# Transform C
1. Update `DataSourceConstructor`:
   1. body: add invocation to `updateADrived()`
2. Add observer method for `aDerived`
3. Add `DerivedAttributeUpdater` method

