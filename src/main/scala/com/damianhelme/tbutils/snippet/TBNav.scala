package com.damianhelme.tbutils.snippet
import scala.xml._
import net.liftweb.util.Helpers._
import scala.xml.transform._
import net.liftweb.common.Logger
import net.liftweb.util.IterableFunc

object TBNav extends Logger {
  
  /* Transforms the XML produced by Menu.build such that any menus defined as:
  Menu("Test") / "test" >> LocGroup("test") >> PlaceHolder submenus (
    Menu("Test 2") / "test2",
    Menu("Test 3") / "test3"
  ),
  or
  Menu("Test") / "test" >> LocGroup("test") >> submenus (
    Menu("Test 2") / "test2",
    Menu("Test 3") / "test3"
  ),
  will be transformed into Twitter Bootstrap dropdown menus
  */

  

  def menuToTBNav( in: NodeSeq ) : NodeSeq = {

    object t1 extends RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {

      // removes the white space which appears between elements 
      case Text(text) if ( text.matches("\\s+") ) => NodeSeq.Empty
      
      /* matches xml of the format:     
        *<li>
           <span>Test</span>
           <ul>
            <li>
               <a href="/test2">Test 2</a>
            </li>
            <li>
               <a href="/test3">Test 3</a>
            </li>
           </ul>
          </li>
      and transforms it to:
        <li class="dropdown" data-dropdown="dropdown">
           <a class="dropdown-toggle">Test</a>
           <ul class="dropdown-menu">
              <li>
                 <a href="/test2">Test 2</a>
              </li>
              <li>
                 <a href="/test3">Test 3</a>
              </li>
           </ul>
        </li>

        */
      case li @ Elem(liPrefix, "li", liAttribs, liScope, 
          span @ Elem(spanPrefix,"span",spanAttribs,spanScope,spanChildren @ _*), 
          ul @ Elem(ulPrefix,"ul",ulAttribs,ulScope,ulChildren @ _*), 
          other @ _* )  => {

            // add the Bootstrap classes to existing attributes
            val newLiAttribs = appendToClass(liAttribs,"dropdown").append("data-dropdown" -> "dropdown")
            val newAAttribs = appendToClass(spanAttribs,"dropdown-toggle")
            val newUlAttribs = appendToClass(ulAttribs,"dropdown-menu")

            // create a new node seq with modified attributes
            Elem(liPrefix,"li",newLiAttribs,liScope, 
              Elem(spanPrefix, "a", newAAttribs, spanScope, spanChildren: _*) ++
              Elem(ulPrefix, "ul", newUlAttribs, ulScope, ulChildren: _*) ++
              other: _*)
          }
       
     /* matches xml of the format:     
        *<li>
           <a href="/test">Test</a>
           <ul>
            <li>
               <a href="/test2">Test 2</a>
            </li>
            <li>
               <a href="/test3">Test 3</a>
            </li>
           </ul>
          </li>
      and transforms it to:
        <li class="dropdown" data-dropdown="dropdown">
           <a href="/test" class="dropdown-toggle">Test</a>
           <ul class="dropdown-menu">
              <li>
                 <a href="/test2">Test 2</a>
              </li>
              <li>
                 <a href="/test3">Test 3</a>
              </li>
           </ul>
        </li>

        */
       case li @ Elem(liPrefix, "li", liAttribs, liScope, 
          a @ Elem(aPrefix,"a",aAttribs,aScope,aChildren @ _*), 
          ul @ Elem(ulPrefix,"ul",ulAttribs,ulScope,ulChildren @ _*), 
          other @ _* )  => {
            // add the Bootstrap classes to existing attributes
            val newLiAttribs = appendToClass(liAttribs,"dropdown").append("data-dropdown" -> "dropdown")
            val newAAttribs = appendToClass(aAttribs,"dropdown-toggle")
            val newUlAttribs = appendToClass(ulAttribs,"dropdown-menu")

            // create a new node seq with modified attributes
            Elem(liPrefix,"li",newLiAttribs,liScope, 
              Elem(aPrefix, "a", newAAttribs, aScope, aChildren: _*) ++
              Elem(ulPrefix, "ul", newUlAttribs, ulScope, ulChildren: _*) ++
              other: _*)
          }
      case other @ _ => other
      }
    }

    // debug("menuToTBNav received: " + new PrettyPrinter(80,3).formatNodes(in))
    object rt1 extends RuleTransformer(t1)
    val out = rt1.transform(in)
    // debug("menuToTBNav out: " + new PrettyPrinter(80,3).formatNodes(out))
    out
  }
  /*
  def menuToTBNav(in: NodeSeq) : NodeSeq = {
     def testNode(ns: NodeSeq, cssSel: String): Boolean = {
    var ret = false // does the NodeSeq have any nodes that match the CSS Selector
    (cssSel #> ((ignore: NodeSeq) => {ret = true; NodeSeq.Empty}))(ns)
    ret
  }
     def childHasUI(ns: NodeSeq) : Boolean = {
       true
     }

     val f = "li [class+]" #>
       (((ns: NodeSeq) => Some("dropdown").filter(ignore => childHasUI(ns))): IterableFunc )
       
     f(in)
  }
  */
  
  // append a new value to the class attribute if one already exists, otherwise create a new class 
  // with the given value
  def appendToClass(attribs: MetaData, newClass: String ) : MetaData = {
      // Note that MetaData.get("class") returns a Option[Seq[Node]] , not Option[Node] as might be expected
      // for an explanation of why see the scala-xml book: 
      val oldClass : Option[String] = attribs.get("class").map(_.mkString).filterNot(_ == "")
      val resultingClass = oldClass.map( _.trim + " ").getOrElse("") + newClass
      attribs.append("class" -> resultingClass)
    } 
  
}