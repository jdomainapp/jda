package jda.modules.mbsl.exceptions;

import java.text.MessageFormat;

import jda.modules.common.exceptions.InfoCode;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public enum DomainMessage implements InfoCode {
  /**0: activity class*/
  ERR_GRAPH_CONFIGURATION_NOT_FOUND_WHEN_REQUIRED("Cấu hình đồ thị hoạt động được yêu cầu nhưng không có: {0}"),
  /**0: node type */
  ERR_NODE_CLASS_NOT_FOUND_FOR_TYPE("Không tìm thấy lớp node nào cho loại: {0}"),
  /**0: node*/
  ERR_NODE_STATE_NOT_VALID("Trạng thái của node không hợp lệ: {0}"),
  /***
   * This error occurs when no out-edge(s) of a node are found suitable to be offered the tokens of the node
   * <br>0: node 
   */
  ERR_NO_SUITABLE_OUT_EDGE("Không có cạnh hành động đầu ra nào của nút phù hợp để xử lí tiếp: {0}"),
  /***
   * This error occurs when the join logic can not be executed on the join input.
   * <br>0: node, 
   * 1: input
   */
  ERR_FAIL_TO_FILTER_JOIN_INPUT("Lỗi xử lí dữ liệu đầu vào của của nút join: {0} (input: {1})"),
  /**0: activity class*/
  ERR_GRAPH_HAS_NO_INITIAL_NODES("Đồ thị hoạt động ({0}) không có nút khởi động"),
  ;

  private String text;

  /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
  private MessageFormat messageFormat;

  private DomainMessage(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }
  
  @Override
  public MessageFormat getMessageFormat() {
    if (messageFormat == null) {
      messageFormat = new MessageFormat(text);
    }
    
    return messageFormat;
  }  
}
