package net.yested.ext.bootstrap3

import net.yested.core.html.*
import net.yested.core.properties.*
import net.yested.core.utils.Div
import org.junit.Test
import spec.*
import kotlin.dom.addClass
import kotlin.dom.appendText

/**
 * A test for [page].
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 3/30/17
 * Time: 10:51 PM
 */
class PageTest {
    @Test
    fun shouldRenderAsExpected() {
        val div = Div {
            div {
                page {
                    navbar(NavbarCompletePosition.FixedTop) {
                        brand { a { href="#"; appendText("Brand") } }
                        menu(NavbarPosition.Right) {
                            item(true.toProperty()) { a { href = "#"; appendText("Nav Item A") } }
                            item { a { href = "#"; appendText("Nav Item B") } }
                            item { a { href = "#"; appendText("Nav Item C") } }
                        }
                    }
                    header { h1 { appendText("Header") } }
                    content { appendText("Content") }
                    footer { appendText("Footer") }
                }
            }
        }
        val expectedHtml = """
<div>
  <div class="container">
    <header class="navbar navbar-fixed-top">
      <a class="btn" data-toggle="collapse" data-target="#navbar">
        <span class="icon-bar"/>
        <span class="icon-bar"/>
        <span class="icon-bar"/>
      </a>
      <a class="brand" href="#">Brand</a>
      <nav id="navbar" class="nav-collapse collapse pull-right" role="navigation">
        <ul class="navbar-nav">
          <li class="active"><a href="#">Nav Item A</a></li>
          <li><a href="#">Nav Item B</a></li>
          <li><a href="#">Nav Item C</a></li>
        </ul>
      </nav>
    </header>
    <div class="page-header">
      <h1>Header</h1>
    </div>
    <div role="document">
      Content
    </div>
    <footer>
      Footer
    </footer>
  </div>
</div>
""".replace(Regex("\r?\n\\s*"), "") + "\n"
        (div.innerHTML + "\n").mustBe(expectedHtml)
    }
}
