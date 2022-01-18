package br.com.landi.books.types

enum class ErrorMessage(val errorMessage: String) {

    FIELD_NECESSARY("Preencha o campo"),
    FINISH_DATE_WRONG("Data final de leitura deve ser igual ou depois da data inicial de leitura")
}