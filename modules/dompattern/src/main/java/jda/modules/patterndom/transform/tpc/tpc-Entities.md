Transformation procedure for the Entities pattern <!-- omit in toc -->
=====

# Input <!-- omit in toc -->
- (if already exists) a Java class `C`
- (if create new) FQN class name 
- id attribute name
# Output 
- `C` contains all elements of the Entity pattern

# 1. Transform C
2. Add annotation `@DClass`: 
   1. update class declaration with `@DClass`
   2. add import statement for `DClass`
3. Add attribute `id`:
   1. update class body with attribute `id` (positioned at the top)
   2. add import statements for `Serializable`, `DAttr`, `DAttr.Type`
4. Add observer method `getId`:
   1. update class body with method `getId`
   2. add import statements for `DOpt`, `AttrRef`
