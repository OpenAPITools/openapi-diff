package org.openapitools.openapidiff.core.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkdownRender extends MarkdownRenderBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownRender.class);
  private static final String H4 = "#### ";
  private static final String H5 = "##### ";
  private static final String H6 = "###### ";
  private static final String BLOCKQUOTE = "> ";
  private static final String CODE = "`";
  private static final String PRE_LI = "    ";
  private static final String LI = "* ";
  private static final String HR = "---\n";

  @Override
  public String getH4() {
    return H4;
  }

  @Override
  public String getH5() {
    return H5;
  }

  @Override
  public String getH6() {
    return H6;
  }

  @Override
  public String getBlockQuote() {
    return BLOCKQUOTE;
  }

  @Override
  public String getCode() {
    return CODE;
  }

  @Override
  public String getPreListItem() {
    return PRE_LI;
  }

  @Override
  public String getListItem() {
    return LI;
  }

  @Override
  public String getHorizontalRule() {
    return HR;
  }
}
