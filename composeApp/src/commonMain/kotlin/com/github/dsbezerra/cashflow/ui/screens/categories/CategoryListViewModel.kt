package com.github.dsbezerra.cashflow.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.repository.CategoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryListViewModel(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryListState())
    val state = _state.asStateFlow()

    private val _events = Channel<CategoryListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var collectJob: Job? = null

    init {
        startCollecting()
    }

    fun onAction(action: CategoryListAction) {
        when (action) {
            CategoryListAction.Refresh -> {
                collectJob?.cancel()
                startCollecting()
            }
        }
    }

    private fun startCollecting() {
        collectJob = viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _state.update {
                    it.copy(
                        active = categories.filter { c -> !c.isArchived },
                        archived = categories.filter { c -> c.isArchived },
                        isLoading = false,
                    )
                }
            }
        }
    }
}
