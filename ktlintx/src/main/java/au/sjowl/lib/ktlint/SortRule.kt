package au.sjowl.lib.ktlint

import com.github.shyiko.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class SortRule : Rule("kotlin-sort") {

    private val sorter = Sorter()

    override fun visit(node: ASTNode, autoCorrect: Boolean, emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit) {

        if (node.elementType == KtStubElementTypes.CLASS) {
            println(classes++)
        }

        if (node.elementType == KtStubElementTypes.CLASS_BODY) {
            val children = node.getChildren(null)
            val innerElements = children.filter { sorter.sortList.contains(it.elementType) }
            val sortedInnerElements = innerElements
                .sortedBy { sorter.getMethodIndex(it) }
                .sortedBy { sorter.getModifierIndex(it) }
                .sortedBy { sorter.sortList.indexOf(it.elementType) }

            if (innerElements != sortedInnerElements) {

                emit(node.startOffset, "Incorrect order of inners", true)

                if (autoCorrect) {
                    innerElements.forEach {
                        node.removeChild(it)
                    }

                    sorter.removeSpaces(node)

                    val start = node.findChildByType(KtTokens.LBRACE)!!.treeNext

                    sortedInnerElements.forEachIndexed { i, astNode ->
                        if (i == 0) node.addChild(PsiWhiteSpaceImpl(sorter.space), start)
                        node.addChild(astNode, start)
                        node.addChild(PsiWhiteSpaceImpl(sorter.space), start)
                    }
                    sorter.removeWhitespacesBefore(node, node.findChildByType(KtTokens.RBRACE)!!)
                }
            }
        }
    }

    companion object {
        var classes = 0
    }
}

/*
* todo
* no spaces between similar modifiers
* some order and spacing in functions
* inherit order overrides
* interfaces - define method order
* */