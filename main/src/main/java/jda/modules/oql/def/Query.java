package jda.modules.oql.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.QueryException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;

/**
 * Represents a search query.
 * <p>
 * A search query is a <b>Boolean AND</b> query consisting of one or more query
 * terms, each of which has the form:
 * 
 * <pre>
 * attribute-name op val
 * </pre>
 * 
 * , where <code>attribute-name</code> is the name of a domain attribute,
 * <code>op</code> is one of the expression operators defined in {@see
 * Expression}, and <code>val</code> is a value to match with that attribute.
 * The query terms are delimited by the keyword <code>&</code> 
 * (meaning <code>AND</code>).
 * 
 * <p>
 * Thus overall, a search query has the following form * 
 * <pre>
 * (attribute-name op val [&])*
 * </pre>
 * 
 * The delimiter keyword <code>&</code> is optional and only needed when more than one query terms are present. 
 * The extra spaces surrounding the operator, if any, are ignored.
 * 
 * <p>
 * For example, the query
 * 
 * <pre>
 * name=nguyen & dob=1990
 * </pre>
 * 
 * means <i>search for all objects whose attribute <code>name</code> matches
 * <code>nguyen</code> and whose attribute <code>dob</code> matches 1990</i>.
 * 
 * <br>
 * A result of this query would contain all <code>Student</code> objects whose
 * family name is <code>nguyen</code> and who were born in <code>1990</code>.
 * 
 */
public class Query<T extends Expression> {
  
  public static final String Char_WildCart = "%";
  
  private List<T> terms;

  // constructor methods
  /**
   * @effects initialises a new empty <code>Query</code> (i.e. no terms)
   */
  public Query() {
    terms = new ArrayList();
  }

  /**
   * @effects initialises a single-term query
   */
  public Query(T exp) {
    this();
    add(exp);
  }

  /**
   * @effects adds <code>Expression</code> <code>exp</code> as a new term in <code>this</code>
   * @param exp
   */
  public void add(T exp) {
    terms.add(exp);
  }

  /**
   * @effects 
   *  remove <tt>exps</tt> from <tt>this.terms</tt>
   * @version 
   * - 3.3: changed to array
   */
  public void remove(T...exps) {
    // v3.3:
    //terms.remove(exp);
    
    for (T exp : exps)
      terms.remove(exp);
  }

  /**
   * @effects 
   *  remove all terms in this
   * @version 3.2
   */
  public void removeAll() {
    terms.clear();
  }
  
  /**
   * @effects adds query terms contained in the query string <code>queryString</code> to <code>this</code>.
   * 
   * @requires <code>queryString</code> is a valid string representation of <code>Query</code>
   * @see #fromString(Query, String)
   */
  public void append(String queryString) throws QueryException {
    Query<Expression> query = (Query<Expression>) this;
    fromString(query, queryString);
  }
  
  /**
   * @effects returns an <code>Iterator<Expression></code> of all the query terms
   */
  public Iterator<T> terms() {
    return terms.iterator();
  }

  /**
   * @effects 
   *  return the number of query terms
   */
  public int size() {
    return terms.size();
  }
  
  /**
   * @effects 
   *  if this contains no terms
   *    return true
   *  else
   *    return false
   *  @version 2.7.4
   */
  public boolean isEmpty() {
    return size()==0;
  }
  
  /**
   * @effects returns <code>Expression</code> term at <code>index</code>
   */
  public T getTerm(int index) {
    if (terms.size() > index) {
      return terms.get(index);
    } else {
      return null;
    }
  }
  
  /**
   * This methods evaluate a value against the query. For <code>AND</code> queries, the input
   * value must satisfy all the query terms. (For <code>OR</code> queries (not supported yet), 
   * on the other hand, 
   * the input value needs to only satisfy one term.)
   * 
   * @effects if <code>o = null</code> returns <code>false</code>, else 
   *          if <code>term.eval(attributeVal) = true</code> for all <code>term</code> in <code>this</code>, 
   *          where <code>attributeVal</code> is the value of the attribute of <code>o</code> that matches <code>term</code> then 
   *          returns <code>true</code>, else returns <code>false</code>.
   *          
   *          <p>Throws NotPossibleException if failed to evaluate o
   */
  @Deprecated
  public boolean eval(DODMBasic schema, Object o) throws NotPossibleException {
    if (o == null) {
      return false;
    }
    
    // AND semantics
    Object attributeVal;
    for (T term: terms) {
      attributeVal = schema.getDsm().getAttributeValue(o, term.getVar());
      if (!term.eval(attributeVal)) {
        return false;
      }
    }
    
    return true;
  }
  
  /**
   * This methods evaluate a value against the query. For <code>AND</code> queries, the input
   * value must satisfy all the query terms. (For <code>OR</code> queries (not supported yet), 
   * on the other hand, 
   * the input value needs to only satisfy one term.)
   * 
   * @effects if <code>o = null</code> returns <code>false</code>, else 
   *          if <code>term.eval(attributeVal) = true</code> for all <code>term</code> in <code>this</code>, 
   *          where <code>attributeVal</code> is the value of the attribute of <code>o</code> that matches <code>term</code> then 
   *          returns <code>true</code>, else returns <code>false</code>.
   *          
   *          <p>Throws NotPossibleException if failed to evaluate o
   */
  public boolean eval(DSMBasic schema, Object o) throws NotPossibleException {
    if (o == null) {
      return false;
    }
    
    // AND semantics
    Object attributeVal;
    for (T term: terms) {
      attributeVal = schema.getAttributeValue(o, term.getVar());
      if (!term.eval(attributeVal)) {
        return false;
      }
    }
    
    return true;
  }
  
  /**
   * Parses a search query string into a <code>Query</code> object.
   * 
   * 
   * @effects if <code>query != null</code> and is well-formed then returns a
   *          <code>Query</code> of the query string <code>queryString</code>,
   *          else if <code>queryString</code> is not well-formed then throws
   *          <code>QueryException</code>, else if <code>query = null</code>
   *          returns <code>null</code>.
   * 
   * @requires <code>query != null</code>
   */
  public static Query<Expression> fromString(final String queryString)
  throws QueryException {
    Query<Expression> query = new Query<Expression>();
    fromString(query, queryString);
    return query;
  }

  /**
   * @effects parses <code>queryString</code> into query terms and adds them to <code>query</code>
   * @requires <code>query != null</code>
   */
  private static void fromString(Query<Expression> query, final String queryString)
      throws QueryException {
    if (queryString == null)
      return;

    String[] terms;
    if (queryString.indexOf("&") > -1) {
      terms = queryString.split("&");
    } else {
      terms = new String[] { queryString };
    }

    String term;
    Expression exp;    
    for (int i = 0; i < terms.length; i++) {
      term = terms[i];
      exp = parseSearchTerm(term);
      query.add(exp);
    }
  }

  /**
   * @effects returns an <code>Expression</code> representing the term
   *          <code>termString</code>, or throws <code>QueryException</code> if
   *          <code>termString</code> is not well formed.
   * 
   * @requires <code>termString != null</code>
   */
  private static Expression parseSearchTerm(String termString)
      throws QueryException {
    Op[] opts = Op.values();
    // look for the operator first
    Op opx = null;
    for (Op opt: opts) {
      if (termString.indexOf(opt.getName()) > -1) { // case-sensitive
        opx = opt;  // found
        break;
      }
    }
    
    if (opx == null) {
      throw new QueryException(QueryException.Code.OPERATOR_NOT_FOUND,
          "Toán tử không đúng trong truy vấn: {0}", termString);
    }

    String[] items = termString.split(opx.getName());
    if (items.length < 2) {
      throw new QueryException(QueryException.Code.QUERY_TERM_NOT_WELL_FORMED,
          "Truy vấn không đúng mẫu cú pháp: {0}", termString);
    }

    String attribute = items[0].trim();
    String val = items[1].trim();

    return new Expression(attribute, opx, val);
  }
  
  public String toString() {
    return toString(true);
  }

  /**
   * @effects 
   *  create and return a string representation of <tt>this</tt>, replacing any <tt>null</tt>
   *  term values by <tt>""</tt> if <tt>withNulls = false</tt>
   */
  public final String toString(boolean withNulls) {
    T term;
    int numTerms = terms.size();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < terms.size(); i++) {
      term = terms.get(i);
      sb.append(term.toString(withNulls));
      if (i < numTerms - 1)
        sb.append(" & ");
    }

    return sb.toString();
  }
  
  /**
   * @requires 
   *  <tt>queryDict != null -> 
   *    for all (s1,s2) in queryDict. s1 is a domain attribute name, s2 is a label of the attribute named s1</tt> 
   * @effects 
   *  create and return a <b>user-friendly</b> string representation of <tt>this</tt>, 
   *  in which each term's variable name is replaced by a language-specific label 
   *  in <tt>queryDict</tt>, if it is specified.
   *  
   * @version 3.1
   */
  public final String toUserFriendlyString(Map<String,String> queryDict) {
    T term;
    int numTerms = terms.size();
    StringBuffer sb = new StringBuffer();
    final String AND = " & ";
    
    for (int i = 0; i < terms.size(); i++) {
      term = terms.get(i);
      sb.append(term.toUserFriendlyString(queryDict));
      if (i < numTerms - 1)
        sb.append(AND);
    }

    return sb.toString();
  }
} // end class Expression
