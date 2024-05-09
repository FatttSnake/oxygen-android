package top.fatweb.oxygen.toolbox.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import top.fatweb.oxygen.toolbox.network.model.ResponseResult

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Fail(val message: String): Result<Nothing>
    data class Error(val exception: Throwable) : Result<Nothing>
    data object Loading : Result<Nothing>
}

fun <T> Flow<ResponseResult<T>>.asResult(): Flow<Result<T>> = map<ResponseResult<T>, Result<T>> {
    if (it.success) {
        Result.Success(it.data!!)
    } else {
        Result.Fail(it.msg)
    }
}
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }

fun <T, R> Result<T>.asExternalModel(block: (T) -> R): Result<R> =
    when (this) {
        is Result.Success -> {
            Result.Success(block(data))
        }

        is Result.Fail -> {
            Result.Fail(message)
        }

        is Result.Error -> {
            Result.Error(exception)
        }

        Result.Loading -> Result.Loading
    }