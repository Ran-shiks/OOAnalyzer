/* Generated By:JJTree: Do not edit this line. ASTPrimaryExpression.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package parser;

public
class ASTPrimaryExpression extends SimpleNode {
  public ASTPrimaryExpression(int id) {
    super(id);
  }

  public ASTPrimaryExpression(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) throws Exception {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=48757f36f0870f4c9bf4d79abd5c6ef7 (do not edit this line) */
