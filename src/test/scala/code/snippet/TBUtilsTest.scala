package com.damianhelme.tbutils.snippet
import scala.xml._
import net.liftweb.util.Helpers._
import scala.xml.transform._
import org.scalatest.FunSuite
import org.scalatest.FeatureSpec
import net.liftweb.common.Logger

class appendToClassTest extends FunSuite with Logger {
  
  test("set class where one previously existed") {
    val att = new UnprefixedAttribute("class",Text("old"),Null)
    expect("old foo") {
      TBNav.appendToClass(att, "foo").get("class").map(_.mkString).get
    }
  }
  test("set class where no class previously existed") {
    val x @ Elem(_,_,a,_,_*)= <li>blah</li>
    expect("foo") {
      TBNav.appendToClass(a, "foo").get("class").map(_.mkString).get
    }
  }
  test("set class where class previously existed but had blank value") {
    val x @ Elem(_,_,a,_,_*)= <li class="">blah</li>
    expect("foo") {
      val ret = TBNav.appendToClass(a, "foo").get("class").map(_.mkString).get
      ret
    }   
    
  }
  
  test("set class where other attributes set") {
    val a = new UnprefixedAttribute("class",Text("aclass"),Null)
    val b = new UnprefixedAttribute("href",Text("bhref"),a)
    expect("aclass foo") {
      val ret = TBNav.appendToClass(a, "foo").get("class").map(_.mkString).get
      ret
    }
  }
} 
  
class menuToNavTest extends FunSuite with Logger {
  test("regular case generated using PlaceHolder"){
    /* aiming for: 
   val target = <ul>
         <li class="dropdown" >
           <a class="dropdown-toggle" data-toggle="dropdown" >Dropdown<b class="caret"></b></a>
           <ul class="dropdown-menu">
              <li><a href="#">Secondary link</a></li>
              <li><a href="#">Something else here</a></li>
              <li class="divider"></li>
              <li><a href="#">Another link</a></li>
            </ul>
            <li>some other</li>
          </li>
        </ul>
        
     * */
   val target = 
       <ul>
         <li class="dropdown" >
           <a class="dropdown-toggle" data-toggle="dropdown" >Dropdown<b class="caret"></b></a>
           <ul class="dropdown-menu">
              <li><a href="#">Secondary link</a></li>
              <li><a href="#">Something else here</a></li>
              <li><a href="#">Another link</a></li>
           </ul>
         </li>
         <li>some other</li>
       </ul>
//      val target : NodeSeq = stripWhiteSpace(<ul><li class="dropdown" data-dropdown="dropdown"><a class="dropdown-toggle">Dropdown</a><ul class="dropdown-menu"><li><a href="#">Secondary link</a></li><li><a href="#">Something else here</a></li><li><a href="#">Another link</a></li></ul></li><li>some other</li></ul>)
    val targetText = new PrettyPrinter(80,3).formatNodes(target)
    val in = <ul>
          <li>
            <span>Dropdown</span>
            <ul>
              <li><a href="#">Secondary link</a></li>
              <li><a href="#">Something else here</a></li>
              <li><a href="#">Another link</a></li>
            </ul>
          </li>
          <li>some other</li>
        </ul>
  
    val result : NodeSeq = (TBNav.menuToTBNav)(in)
    val resultText : String = new PrettyPrinter(80,3).formatNodes(result)
    
    // note that we're comparing text representations to avoid mismatches due to whitespace between elements
    expect(targetText) {
      resultText
   }
  }
  
  test("regular case using generated without PlaceHolder"){
    // aiming for: 
   val target = 
       <ul>
         <li class="dropdown" >
           <a class="dropdown-toggle" data-toggle="dropdown" href="/dropdown">Dropdown<b class="caret"></b></a>
           <ul class="dropdown-menu">
              <li><a href="#">Secondary link</a></li>
              <li><a href="#">Something else here</a></li>
              <li><a href="#">Another link</a></li>
           </ul>
         </li>
         <li>some other</li>
       </ul>    
    val targetText = new PrettyPrinter(80,3).formatNodes(target)
       
    val in = <ul>
          <li>
            <a href="/dropdown" >Dropdown</a>
            <ul>
              <li><a href="#">Secondary link</a></li>
              <li><a href="#">Something else here</a></li>
              <li><a href="#">Another link</a></li>
            </ul>
          </li>
          <li>some other</li>
        </ul>
  
    val result : NodeSeq = (TBNav.menuToTBNav)(in)
    val resultText : String = new PrettyPrinter(80,3).formatNodes(result)
 
    // note that we're comparing text representations to avoid mismatches due to whitespace between elements
    expect(targetText) {
      resultText
    }
  }
    
  test("non matching nodeseq stays the same") {
     val in = <ul>
          <li><a href="/test" >Test</a></li>
          <li>some other item</li>
        </ul>
     val result : NodeSeq = (TBNav.menuToTBNav)(in)
     val target : NodeSeq = stripWhiteSpace(in)
     
     expect(target) {
      result
     }
  }

  // utility class to remove white space text elements between other elements
  def stripWhiteSpace( in: NodeSeq ) : NodeSeq = {

    object t1 extends RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {

      // removes the white space which appears between elements 
      case Text(text) if ( text.matches("\\s+") ) => NodeSeq.Empty
     
      case other @ _ => other
      }
    }

    // debug("stripWhiteSpace received: " + new PrettyPrinter(80,3).formatNodes(in))
    object rt1 extends RuleTransformer(t1)
    val out = rt1.transform(in)
    // debug("stripWhiteSpace out: " + new PrettyPrinter(80,3).formatNodes(out))
    out
  }
 
}