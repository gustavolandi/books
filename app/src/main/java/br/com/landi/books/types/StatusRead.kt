package br.com.landi.books.types

enum class StatusRead(val status: String) {

    STATUS_NOT_INITIALIZED("Não Iniciado"),
    STATUS_READING("Lendo"),
    STATUS_FINISHED("Lido")

}

class GetStatus {

    companion object {
        fun getStatus(text: String): StatusRead {
            for (statusRead in StatusRead.values()) {
                if (statusRead.status == text) {
                    return statusRead
                }
            }
            return StatusRead.STATUS_NOT_INITIALIZED
        }

        fun getStatus() : List<String> {
            return StatusRead.values().map {
                if (it.status.isNotEmpty()) {
                    it.status
                } else {
                    "Não Iniciado"
                }
            }
        }
    }
}

