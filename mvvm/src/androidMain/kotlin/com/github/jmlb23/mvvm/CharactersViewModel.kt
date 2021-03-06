package com.github.jmlb23.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jmlb23.marvel.domain.entity.Character
import com.github.jmlb23.marvel.domain.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

actual class CharactersViewModel actual constructor(private val getCharactersPaginated: UseCase<Int, List<Character>>) :
    ViewModel() {
    private val currentPage = MutableStateFlow(0)
    actual val elements = MutableStateFlow(listOf<Character>())
    actual val error = MutableStateFlow<String?>(null)

    private fun getCurrentPage() = currentPage.value

    actual fun nextPage() {
        viewModelScope.launch(Dispatchers.IO) {
            getCharactersPaginated.exec(currentPage.value).fold({
                elements.value = elements.value + it
                currentPage.value = getCurrentPage() + 1
            }, {
                error.value = it.message ?: ""
            })
        }
    }

    actual fun getElement(subcription: (List<Character>) -> Unit) {
        elements.onEach(subcription).launchIn(viewModelScope)
    }
}