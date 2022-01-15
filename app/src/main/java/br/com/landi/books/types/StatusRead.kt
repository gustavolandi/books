package br.com.landi.books.types

enum class StatusRead(val status: String) {

    STATUS_NOT_INITIALIZED(""),
    STATUS_READING("Lendo"),
    STATUS_FINISHED("Lido")

}