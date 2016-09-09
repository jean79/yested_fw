package net.yested.core.utils

import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import kotlin.browser.window
import kotlin.dom.appendText

fun Node.removeAllChildElements() {
    while (this.firstChild != null) {
        this.removeChild(this.firstChild!!)
    }
}

fun HTMLElement.removeChildByName(childElementName:String) {
   val elements = this.getElementsByTagName(childElementName)
   (0..elements.length-1).forEach {
       this.removeChild(elements.get(it)!!)
   }
}

fun Node.setChild(child: Node) {
   removeAllChildElements()
   appendChild(child)
}

fun HTMLElement.setContent(content: String) {
   removeAllChildElements()
   appendText(content)
}

fun HTMLElement.whenAddedToDom(run: () -> Unit) {
   repeatWithDelayUntil (
           check = { isIncludedInDOM(this) },
           millisecondInterval = 100,
           run = run
   )
}

fun isIncludedInDOM(node:Node):Boolean {
   /*var style = window.getComputedStyle(node);
   return style.display != "none" && style.display != ""*/
   return (node as HTMLElement).offsetParent != null
}

fun repeatWithDelayUntil(check:()->Boolean, millisecondInterval:Int, run:()->Unit) {
   if (check()) {
       run()
   } else {
       window.setTimeout({repeatWithDelayUntil(check, millisecondInterval, run)}, millisecondInterval)
   }
}
