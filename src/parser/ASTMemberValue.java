/* Generated By:JJTree: Do not edit this line. ASTMemberValue.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package parser;

public
class ASTMemberValue extends SimpleNode {
  public ASTMemberValue(int id) {
    super(id);
  }

  public ASTMemberValue(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) throws Exception {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=0ff7e450f10f9297ec303f2925c6cc57 (do not edit this line) */
