package com.aca.arabamsat.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Repository.FakeAdRepository
import com.aca.arabamsat.Repository.FakeUserRepository
import com.aca.arabamsat.getOrAwaitValueTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest{

    private lateinit var viewModel : DetailViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        viewModel = DetailViewModel(FakeUserRepository())
    }

    @Test
    fun `adding ad to favorite returns true`(){
        viewModel.addAdToFavorite("1213")
        var value = viewModel.isAdFavorite("1213").getOrAwaitValueTest()
        assert(value)
    }

    @Test
    fun `adding ad to favorite difrent id returns false`(){
        viewModel.addAdToFavorite("1213")
        var value = viewModel.isAdFavorite("1214").getOrAwaitValueTest()
        assertFalse(value)
    }
}