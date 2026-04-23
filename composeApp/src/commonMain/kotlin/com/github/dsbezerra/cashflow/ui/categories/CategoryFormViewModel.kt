package com.github.dsbezerra.cashflow.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dsbezerra.cashflow.domain.model.Category
import com.github.dsbezerra.cashflow.domain.repository.CategoryRepository
import com.github.dsbezerra.cashflow.util.generateId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryFormViewModel(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryFormState())
    val state = _state.asStateFlow()

    private val _events = Channel<CategoryFormEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun initialize(categoryId: String?) {
        viewModelScope.launch {
            val all = categoryRepository.getAll().first()

            if (categoryId == null) {
                // New category — parents are non-archived, non-default root categories
                val parents = all.filter { !it.isArchived && it.parentId == null }
                _state.update { it.copy(availableParents = parents) }
                return@launch
            }

            val category = all.find { it.id == categoryId } ?: return@launch
            val parents = all.filter { c ->
                !c.isArchived && c.parentId == null && c.id != categoryId
            }
            _state.update {
                it.copy(
                    categoryId = category.id,
                    name = category.name,
                    type = category.type,
                    icon = category.icon,
                    color = category.color,
                    parentId = category.parentId,
                    isArchived = category.isArchived,
                    isDefault = category.isDefault,
                    isEditMode = true,
                    availableParents = parents,
                )
            }
        }
    }

    fun onAction(action: CategoryFormAction) {
        when (action) {
            is CategoryFormAction.NameChanged -> _state.update { it.copy(name = action.name, nameError = null) }
            is CategoryFormAction.TypeChanged -> _state.update { it.copy(type = action.type, parentId = null) }
            is CategoryFormAction.IconChanged -> _state.update { it.copy(icon = action.icon) }
            is CategoryFormAction.ColorChanged -> _state.update { it.copy(color = action.color) }
            is CategoryFormAction.ParentChanged -> _state.update { it.copy(parentId = action.parentId) }
            is CategoryFormAction.ArchivedChanged -> _state.update { it.copy(isArchived = action.isArchived) }
            CategoryFormAction.Save -> save()
            CategoryFormAction.ConfirmDelete -> delete()
        }
    }

    private fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = "Nome é obrigatório") }
            return
        }

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            runCatching {
                if (s.isEditMode) {
                    val existing = categoryRepository.getById(s.categoryId!!)!!
                    categoryRepository.update(
                        existing.copy(
                            name = s.name,
                            type = s.type,
                            icon = s.icon,
                            color = s.color,
                            parentId = s.parentId,
                            isArchived = s.isArchived,
                        )
                    )
                } else {
                    categoryRepository.insert(
                        Category(
                            id = generateId(),
                            name = s.name,
                            type = s.type,
                            icon = s.icon,
                            color = s.color,
                            parentId = s.parentId,
                            isDefault = false,
                            isArchived = false,
                        )
                    )
                }
            }.onSuccess {
                _events.send(CategoryFormEvent.NavigateBack)
            }.onFailure {
                _state.update { st -> st.copy(isSaving = false) }
                _events.send(CategoryFormEvent.ShowError("Falha ao salvar categoria"))
            }
        }
    }

    private fun delete() {
        val id = _state.value.categoryId ?: return
        viewModelScope.launch {
            runCatching { categoryRepository.delete(id) }
                .onSuccess { _events.send(CategoryFormEvent.NavigateBack) }
                .onFailure { _events.send(CategoryFormEvent.ShowError("Falha ao excluir categoria")) }
        }
    }
}
