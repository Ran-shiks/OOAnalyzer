/* Generated By:JJTree: Do not edit this line. ASTCompilationUnit.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package parser;

public
class ASTCompilationUnit extends SimpleNode {
  public ASTCompilationUnit(int id) {
    super(id);
  }

  public ASTCompilationUnit(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) throws Exception {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c5a1d4d7c308fb3366cb0b07bb8681b5 (do not edit this line) */
