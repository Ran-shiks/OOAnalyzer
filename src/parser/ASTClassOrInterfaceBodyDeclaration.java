/* Generated By:JJTree: Do not edit this line. ASTClassOrInterfaceBodyDeclaration.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package parser;

public
class ASTClassOrInterfaceBodyDeclaration extends SimpleNode {
  public ASTClassOrInterfaceBodyDeclaration(int id) {
    super(id);
  }

  public ASTClassOrInterfaceBodyDeclaration(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) throws Exception {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1620d4589aecd635e78ddd6d0d21e697 (do not edit this line) */
