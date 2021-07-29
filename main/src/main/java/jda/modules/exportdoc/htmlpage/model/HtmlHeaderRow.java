package jda.modules.exportdoc.htmlpage.model;

import java.util.ArrayList;
import java.util.List;

public class HtmlHeaderRow {
  
  private List<HtmlHeader> headers;
  private int height;

  public HtmlHeaderRow() {
    headers = new ArrayList<>();
  }
  
  public int size() {
    return headers.size();
  }

  public HtmlHeader get(int i) {
    return headers.get(i);
  }

  public void add(HtmlHeader header) {
    headers.add(header);
  }

  public void remove(int i) {
    headers.remove(i);
  }

  public void append(HtmlHeaderRow myHeader) {
    headers.addAll(myHeader.headers);
  }

  public void append(int atIndex, HtmlHeaderRow myHeader) {
    headers.addAll(atIndex, myHeader.headers);
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String toString() {
    StringBuffer rh = new StringBuffer();
    for (HtmlHeader header : headers) rh.append(header.getBuffer());
    
    return rh.toString();
  }
}
