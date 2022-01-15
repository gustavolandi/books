package br.com.landi.books.model

import br.com.landi.books.types.StatusRead

data class Book(val id: Long, val title: String, val authorName: String, val registerDate : String, val genreList : MutableList<String>, val readList: MutableList<Read>) {
}

data class Read(val id: Long, val titleBook : String, val authorName: String, val idBook: Long, val startedDate : String, val finishedDate : String, val status : StatusRead) {
}