package com.github.jmlb23.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jmlb23.marvel.domain.entity.Character
import com.github.jmlb23.marvel.domain.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

actual class CharacterViewModel(actual val getDetailUseCase: UseCase<Long, Character?>) :
    ViewModel() {
    private val _detail = MutableStateFlow<Character?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.filterNotNull()
    val detail = _detail.filterNotNull()

    actual fun getDetail(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            getDetailUseCase.exec(id).fold({
                _detail.value = it
            }, {
                _errorMessage.value = it.message
            })
        }
    }

    actual fun detail(callback: (Character) -> Unit, errorCallback: (String) -> Unit) {
        errorMessage.onEach { errorCallback(it) }.launchIn(viewModelScope)
        detail.onEach { callback(it) }.launchIn(viewModelScope)
    }
}