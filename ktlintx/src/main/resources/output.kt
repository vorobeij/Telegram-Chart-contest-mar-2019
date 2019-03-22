package au.sjowl.lib.ktlint.test.output

abstract class Foo {
    abstract fun fooAbstract_1()
    abstract fun fooAbstract_2()
}

interface Interface_1 {
    fun i_1()
    fun i_2()
}

class Bar : Foo(), Interface_1 {

    override fun fooAbstract_1() {
    }

    override fun fooAbstract_2() {
    }

    override fun i_1() {
    }

    override fun i_2() {
    }
}