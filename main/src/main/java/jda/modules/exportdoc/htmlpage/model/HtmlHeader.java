package jda.modules.exportdoc.htmlpage.model;

import jda.modules.exportdoc.util.table.HeaderCell;

/**
 * @overview
 *    This helper class represents the Html content of a <tt>HeaderCell</tt>.
 *     
 * @author dmle
 */
public class HtmlHeader {
  private HeaderCell hc;
  private StringBuffer buffer;
  
  public HtmlHeader(StringBuffer buffer, HeaderCell hc) {
    this.buffer = buffer;
    this.hc = hc;
  }
  
  public HeaderCell getHc() {
    return hc;
  }

  public void setHc(HeaderCell hc) {
    this.hc = hc;
  }

  public String toString() {
    return buffer.toString();
  }

  public StringBuffer getBuffer() {
    return buffer;
  }

  public void setBuffer(StringBuffer buffer) {
    this.buffer = buffer;
  }
}