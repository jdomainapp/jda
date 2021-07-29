/**
 * 
 */
package jda.modules.dcsl.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *
 * @author dmle
 *
 * @version (JavaParser 3.2.5)
 */
public class ParserConstants {
  
  private ParserConstants() {}
  
  public static final Type TypeString = JavaParser.parseClassOrInterfaceType(DAttr.Type.String.name());//new ClassOrInterfaceType("String");
  public static final Type TypeVoid = new VoidType();
  /**primitive type: int*/
  public static final Type TypeInt = new PrimitiveType(Primitive.INT);
  /**Wrapper type: Integer*/
  public static final Type TypeInteger = JavaParser.parseClassOrInterfaceType(DAttr.Type.Integer.name());//new ClassOrInterfaceType("Integer");
  /**primitive type: double*/
  public static final Type Typedouble = new PrimitiveType(Primitive.DOUBLE);
  /**Wrapper type: Double*/
  public static final Type TypeDouble = JavaParser.parseClassOrInterfaceType(DAttr.Type.Double.name());//new ClassOrInterfaceType("Integer");
  /**primitive type: boolean*/
  public static final Type TypeBool = new PrimitiveType(Primitive.BOOLEAN);
  /**Wrapper type: Boolean*/
  public static final Type TypeBoolean = JavaParser.parseClassOrInterfaceType(DAttr.Type.Boolean.name());//new ClassOrInterfaceType("Boolean");

  /**String[]*/
  public static final Type TypeStringArr = JavaParser.parseType("String[]");

  /**Serializable*/
  public static final Type TypeSerializable = JavaParser.parseClassOrInterfaceType(DAttr.Type.Serializable.name());//new ClassOrInterfaceType("Integer");

  public static final Modifier[] modPrivateStatic = {Modifier.PRIVATE, Modifier.STATIC};
  
  public static final Modifier[] modPublicStatic = {Modifier.PUBLIC, Modifier.STATIC};
  /**all declared constructors in a class */
  public static final String Constructor_Any = null;
}
