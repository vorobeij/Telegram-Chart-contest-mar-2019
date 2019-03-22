package au.sjowl.lib.ktlint.test.input

abstract class Foo {
    abstract val fooVal: Int
    abstract val fooVal2: Int
    abstract fun fooAbstract_1()
    abstract fun fooAbstract_2()
}

interface Interface_1 {
    fun i_1()
    fun i_2()
}

open class Bar : Foo(), Interface_1 {

    override val fooVal: Int get() = 0
    override val fooVal2: Int get() = 1

    val barVal = 2
    val barVal2 = 3

    protected val barProtectedVal = 4
    protected val barProtectedVal2 = 5

    private val barPrivateVal = 6
    private val barPrivateVal2 = 7

    override fun fooAbstract_1() {
    }

    override fun fooAbstract_2() {
    }

    override fun i_1() {
    }

    override fun i_2() {
    }

    fun bar() {}

    protected fun barProtectedFun() {}

    private fun barPrivateFun() {}
}

/*
- val/var
  - abstract
  - override
  - no modifiers
  - protected
  - private
- fun
  - abstract
  - override lifecycle
  - override other
  - no modifiers
  - protected
  - private
- object, assert objects, use extension functions or fabrics

 */