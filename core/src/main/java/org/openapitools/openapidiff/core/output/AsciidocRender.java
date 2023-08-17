package org.openapitools.openapidiff.core.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciidocRender extends MarkdownRenderBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(AsciidocRender.class);
  private static final String H4 = "==== ";
  private static final String H5 = "===== ";
  private static final String H6 = "====== ";
  private static final String CODE = "`";
  private static final String PRE_LI = "*";
  private static final String LI = "* ";
  private static final String HR = "\n'''\n";

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
    throw new UnsupportedOperationException(
        "Asciidoc does not use blockquote marker;"
            + "instead it uses blocks like this: \n\n----\nQUOTED TEXT\n----\n");
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

  @Override
  protected String blockquote(String beginning, String text) {
    return beginning + "\n----\n" + text.trim() + "\n----\n";
  }
}
