/* Generated By:JJTree: Do not edit this line. ASTExtendsList.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package parser;

public
class ASTExtendsList extends SimpleNode {
  public ASTExtendsList(int id) {
    super(id);
  }

  public ASTExtendsList(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) throws Exception {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7bb8ccb719e6d5298f3865627a8024e0 (do not edit this line) */
