/* Generated By:JJTree: Do not edit this line. ASTAnnotationTypeBody.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package parser;

public
class ASTAnnotationTypeBody extends SimpleNode {
  public ASTAnnotationTypeBody(int id) {
    super(id);
  }

  public ASTAnnotationTypeBody(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) throws Exception {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1ec0df3adc88862d7d1cfc7fe1eec511 (do not edit this line) */
