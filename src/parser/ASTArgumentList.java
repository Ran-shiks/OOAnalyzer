/* Generated By:JJTree: Do not edit this line. ASTArgumentList.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package parser;

public
class ASTArgumentList extends SimpleNode {
  public ASTArgumentList(int id) {
    super(id);
  }

  public ASTArgumentList(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) throws Exception {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=a1fb064a7fa527fffa70f857a0bbb285 (do not edit this line) */
