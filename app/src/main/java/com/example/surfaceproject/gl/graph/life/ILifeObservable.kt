package com.example.surfaceproject.gl.graph.life

interface ILifeObservable {
    fun dispatch()
    fun addObserver()
    fun removeObserver()
}