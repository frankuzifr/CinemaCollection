package space.frankuzi.cinemacollection.structs

data class LoadingError (
    val errorType: ErrorType,
    val errorCode: Int = 0
    )