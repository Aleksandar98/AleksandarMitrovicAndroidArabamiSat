package com.aca.arabamsat.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Repository.AdRepository
import com.aca.arabamsat.Repository.FakeAdRepository
import com.aca.arabamsat.getOrAwaitValueTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddingViewModelTest{

    private lateinit var viewModel : AddingViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        viewModel = AddingViewModel(FakeAdRepository())
    }

    @Test
    fun `when uploading ad returns false when done uploading`(){
       val value = viewModel.uploadAd(Ad("1234","2020","Model1","1000","4321","0000","Desc1","User1", emptyList(),0.0,0.0),
            mutableListOf()
        ).getOrAwaitValueTest()

        assertFalse(value)
    }
}