package com.sozonov.gitlabx.projects

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sozonov.gitlabx.projects.model.ProjectModel
import com.sozonov.gitlabx.projects.repository.IProjectsRepository
import com.sozonov.gitlabx.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectsViewModel(private val projectsRepository: IProjectsRepository) : BaseViewModel() {

    private val mUpdatedProjects = MutableStateFlow(emptyList<ProjectModel>())
    private val mLoadingState = mutableStateOf(false)
    val loadingState: State<Boolean> = mLoadingState

    val projects = combine(flow {
        mLoadingState.value = true
        emit(projectsRepository.getProjects())
        mLoadingState.value = false
    }, mUpdatedProjects) { initial, updated ->
        if (updated.isEmpty() && initial.isNotEmpty()) {
            initial
        } else {
            updated
        }
    }.stateIn(viewModelScopeWithCEH, SharingStarted.Lazily, emptyList())


    fun updateProjects() {
        viewModelScopeWithCEH.launch {
            mLoadingState.value = true
            mUpdatedProjects.emit(projectsRepository.getProjects())
            mLoadingState.value = false
        }
    }
}

