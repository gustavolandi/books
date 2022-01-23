package br.com.landi.books.model

import br.com.landi.books.types.StatusRead

data class Book(val id: Long, val title: String?, val authorName: String?, val registerDate : String?, val genreList : MutableList<String> = mutableListOf(), val readList: MutableList<Read> = mutableListOf(), val collectionName : String? = "") {
}

data class Read(val id: Long = 0, val titleBook : String? = "", val authorName: String? = "", val idBook: Long = 0, val startedDate : String?, val finishedDate : String?, val status : StatusRead, val genreList : MutableList<String> = mutableListOf(), val collectionName : String? = "") {
}

data class Genre(val id: Long,val genre: String)

data class CollectionBook(val id: Long,val collectionName: String)