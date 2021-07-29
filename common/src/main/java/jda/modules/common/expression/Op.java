package jda.modules.common.expression;

/**Expresion operator constants*/
public enum Op {
  /**equal*/
  EQ("="), //
  /**less than*/
  LT("<"), //
  /**less than or equal*/
  LTEQ("<="), //
  /**greater than*/
  GT(">"), //
  /**greater than or equal*/
  GTEQ(">="), //
  /**not a member of (e.g. x not in {a, b, c})*/
  NOIN("not in"), //
  /** member of (e.g. x in {a,b,c})
   * @version 3.1
   */
  IN("in"),
  MATCH("\u2243"), //
  CONTAINS("\u2287"), //
  /** not equal to*/
  NOTEQ("<>"),
  /**between [a,b]*/
  BETWEEN("in"),
  /**
   * A special non-executable operator that is used for special types of expressions
   * @version 3.1
   */
  Nil("nill"), 
  ;    
  private String name;
  private Op(String n) {
    name = n;
  }
  public String getName() { return name; }    
}